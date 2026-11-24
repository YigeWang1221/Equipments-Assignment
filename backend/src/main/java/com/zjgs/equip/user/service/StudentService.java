package com.zjgs.equip.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zjgs.equip.auth.UserPrincipal;
import com.zjgs.equip.clazz.dto.ClassVO;
import com.zjgs.equip.clazz.dto.GroupVO;
import com.zjgs.equip.clazz.entity.ClassGroup;
import com.zjgs.equip.clazz.entity.ClassInfo;
import com.zjgs.equip.clazz.entity.StudentClass;
import com.zjgs.equip.clazz.mapper.ClassGroupMapper;
import com.zjgs.equip.clazz.mapper.ClassInfoMapper;
import com.zjgs.equip.clazz.mapper.StudentClassMapper;
import com.zjgs.equip.clazz.service.ClassService;
import com.zjgs.equip.clazz.service.GroupService;
import com.zjgs.equip.clazz.support.ClassGuard;
import com.zjgs.equip.common.BusinessException;
import com.zjgs.equip.common.ErrorCode;
import com.zjgs.equip.user.dto.StudentRequest;
import com.zjgs.equip.user.dto.StudentVO;
import com.zjgs.equip.user.entity.Student;
import com.zjgs.equip.user.mapper.StudentMapper;
import com.zjgs.equip.ws.RealtimePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 学生与班级/小组关系业务逻辑。
 */
@Service
@RequiredArgsConstructor
public class StudentService {
    public static final String DEFAULT_PASSWORD = "123456";

    private final StudentMapper studentMapper;
    private final StudentClassMapper studentClassMapper;
    private final ClassGroupMapper classGroupMapper;
    private final ClassInfoMapper classInfoMapper;
    private final ClassGuard classGuard;
    private final ClassService classService;
    private final GroupService groupService;
    private final PasswordEncoder passwordEncoder;
    private final RealtimePublisher realtimePublisher;

    /** 班级学生列表。 */
    public List<StudentVO> listByClass(Long classId) {
        classGuard.requireClass(classId);
        List<StudentClass> mappings = studentClassMapper.selectList(new LambdaQueryWrapper<StudentClass>()
                .eq(StudentClass::getClassId, classId)
                .orderByAsc(StudentClass::getStuId));
        if (mappings.isEmpty()) {
            return new ArrayList<>();
        }
        List<Student> students = studentMapper.selectBatchIds(
                mappings.stream().map(StudentClass::getStuId).toList());
        Map<Long, Student> studentMap = new HashMap<>();
        for (Student student : students) {
            studentMap.put(student.getStuId(), student);
        }
        List<StudentVO> result = new ArrayList<>();
        for (StudentClass mapping : mappings) {
            Student student = studentMap.get(mapping.getStuId());
            if (student != null) {
                result.add(toVO(student, mapping));
            }
        }
        return result;
    }

    /** classId 为空时返回全部学生（教师端）。 */
    public List<StudentVO> list(Long classId) {
        List<Student> students;
        Map<Long, StudentClass> mappingByStudent = new HashMap<>();
        if (classId != null) {
            return listByClass(classId);
        }
        students = studentMapper.selectList(new LambdaQueryWrapper<Student>()
                .orderByAsc(Student::getStuId));
        if (students.isEmpty()) {
            return new ArrayList<>();
        }
        List<StudentClass> allMappings = studentClassMapper.selectList(new LambdaQueryWrapper<StudentClass>()
                .orderByDesc(StudentClass::getJoinedAt)
                .orderByDesc(StudentClass::getId));
        for (StudentClass mapping : allMappings) {
            mappingByStudent.putIfAbsent(mapping.getStuId(), mapping);
        }
        List<StudentVO> result = new ArrayList<>();
        for (Student student : students) {
            result.add(toVO(student, mappingByStudent.get(student.getStuId())));
        }
        return result;
    }

    @Transactional
    public StudentVO create(StudentRequest request, Long teacherId) {
        if (request.getStuId() == null) {
            throw new BusinessException("学号不能为空");
        }
        return save(request.getStuId(), request, teacherId);
    }

    @Transactional
    public StudentVO update(Long stuId, StudentRequest request, Long teacherId) {
        if (request.getStuId() != null && !Objects.equals(request.getStuId(), stuId)) {
            throw new BusinessException("请求体中的学号与路径不一致");
        }
        return save(stuId, request, teacherId);
    }

    @Transactional
    public void delete(Long stuId, Long classId, Long teacherId) {
        StudentClass mapping = classId != null
                ? classGuard.studentClassOf(stuId, classId)
                : classGuard.latestStudentClass(stuId);
        if (mapping == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "学生不在该班级中");
        }
        classGuard.requireOwnedClass(mapping.getClassId(), teacherId);
        Long groupId = mapping.getGroupId();
        studentClassMapper.deleteById(mapping.getId());
        if (groupId != null) {
            groupService.recomputeGroup(groupId);
        }
        long remaining = studentClassMapper.selectCount(new LambdaQueryWrapper<StudentClass>()
                .eq(StudentClass::getStuId, stuId));
        if (remaining == 0) {
            try {
                studentMapper.deleteById(stuId);
            } catch (DataIntegrityViolationException e) {
                throw new BusinessException(ErrorCode.CONFLICT, "该学生存在借用等关联记录，无法删除");
            }
        }
        realtimePublisher.publishClassEvent(mapping.getClassId(), "student.updated",
                Map.of("stuId", stuId, "classId", mapping.getClassId()));
    }

    public ClassVO getClassOfStudent(Long stuId, UserPrincipal principal) {
        classGuard.assertTeacherOrSelf(principal, stuId);
        StudentClass mapping = classGuard.latestStudentClass(stuId);
        if (mapping == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "学生尚未加入任何班级");
        }
        return classService.get(mapping.getClassId());
    }

    public GroupVO getGroupOfStudent(Long stuId, UserPrincipal principal) {
        classGuard.assertTeacherOrSelf(principal, stuId);
        StudentClass mapping = classGuard.latestStudentClass(stuId);
        if (mapping == null || mapping.getGroupId() == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "学生尚未加入任何小组");
        }
        return groupService.get(mapping.getGroupId());
    }

    /**
     * Excel 导入用：更新或创建学生并写入班级映射（不做教师权限校验，由调用方完成）。
     */
    @Transactional
    public Student importUpsert(Long stuId, String stuName, String phone, String college,
                                String adminClassName, Long classId, String teachingClassName) {
        validateStuId(stuId);
        ClassInfo classInfo = classInfoMapper.selectById(classId);
        if (classInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "班级不存在");
        }
        String name = trimToNull(stuName);
        Student student = studentMapper.selectById(stuId);
        LocalDateTime now = LocalDateTime.now();
        if (student == null) {
            if (name == null) {
                throw new BusinessException("学生姓名不能为空");
            }
            student = new Student();
            student.setStuId(stuId);
            student.setStuName(name);
            student.setPhone(trimToNull(phone));
            student.setCollege(trimToNull(college));
            student.setAdminClassName(trimToNull(adminClassName));
            student.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
            student.setStatus(1);
            student.setCreatedAt(now);
            student.setUpdatedAt(now);
            studentMapper.insert(student);
        } else {
            if (name != null) {
                student.setStuName(name);
            }
            if (trimToNull(phone) != null) {
                student.setPhone(trimToNull(phone));
            }
            if (trimToNull(college) != null) {
                student.setCollege(trimToNull(college));
            }
            if (trimToNull(adminClassName) != null) {
                student.setAdminClassName(trimToNull(adminClassName));
            }
            student.setUpdatedAt(now);
            studentMapper.updateById(student);
        }
        upsertMapping(stuId, classId, trimToNull(teachingClassName));
        return studentMapper.selectById(stuId);
    }

    private StudentVO save(Long stuId, StudentRequest request, Long teacherId) {
        validateStuId(stuId);
        ClassInfo classInfo = classGuard.requireOwnedClass(request.getClassId(), teacherId);
        ClassGroup group = null;
        if (request.getGroupId() != null) {
            group = classGuard.requireGroup(request.getGroupId());
            if (!Objects.equals(group.getClassId(), classInfo.getId())) {
                throw new BusinessException("小组不属于所选班级");
            }
        }

        Student student = studentMapper.selectById(stuId);
        LocalDateTime now = LocalDateTime.now();
        if (student == null) {
            student = new Student();
            student.setStuId(stuId);
            student.setStuName(request.getStuName().trim());
            student.setPhone(trimToNull(request.getPhone()));
            student.setCollege(trimToNull(request.getCollege()));
            student.setAdminClassName(trimToNull(request.getAdminClassName()));
            String password = trimToNull(request.getPassword());
            student.setPassword(passwordEncoder.encode(password == null ? DEFAULT_PASSWORD : password));
            student.setStatus(1);
            student.setCreatedAt(now);
            student.setUpdatedAt(now);
            studentMapper.insert(student);
        } else {
            LambdaUpdateWrapper<Student> update = new LambdaUpdateWrapper<Student>()
                    .eq(Student::getStuId, stuId)
                    .set(Student::getStuName, request.getStuName().trim())
                    .set(Student::getUpdatedAt, now);
            // 字段为 null 表示前端未提交，保留原值；空字符串表示清空。
            if (request.getPhone() != null) {
                update.set(Student::getPhone, trimToNull(request.getPhone()));
            }
            if (request.getCollege() != null) {
                update.set(Student::getCollege, trimToNull(request.getCollege()));
            }
            if (request.getAdminClassName() != null) {
                update.set(Student::getAdminClassName, trimToNull(request.getAdminClassName()));
            }
            String password = trimToNull(request.getPassword());
            if (password != null) {
                update.set(Student::getPassword, passwordEncoder.encode(password));
            }
            studentMapper.update(null, update);
        }

        StudentClass mapping = upsertMapping(stuId, classInfo.getId(), trimToNull(request.getTeachingClassName()));
        if (group != null) {
            groupService.assignStudentToGroup(group.getId(), stuId);
            mapping = classGuard.studentClassOf(stuId, classInfo.getId());
        }
        realtimePublisher.publishClassEvent(classInfo.getId(), "student.updated",
                Map.of("stuId", stuId, "classId", classInfo.getId()));
        return toVO(studentMapper.selectById(stuId), mapping);
    }

    private StudentClass upsertMapping(Long stuId, Long classId, String teachingClassName) {
        StudentClass mapping = classGuard.studentClassOf(stuId, classId);
        LocalDateTime now = LocalDateTime.now();
        if (mapping == null) {
            mapping = new StudentClass();
            mapping.setStuId(stuId);
            mapping.setClassId(classId);
            mapping.setTeachingClassName(teachingClassName);
            mapping.setJoinedAt(now);
            studentClassMapper.insert(mapping);
        } else {
            if (teachingClassName != null) {
                mapping.setTeachingClassName(teachingClassName);
            }
            studentClassMapper.updateById(mapping);
        }
        return classGuard.studentClassOf(stuId, classId);
    }

    private StudentVO toVO(Student student, StudentClass mapping) {
        ClassInfo classInfo = mapping == null ? null : classInfoMapper.selectById(mapping.getClassId());
        ClassGroup group = mapping == null || mapping.getGroupId() == null
                ? null : classGroupMapper.selectById(mapping.getGroupId());
        return StudentVO.builder()
                .stuId(student.getStuId())
                .stuName(student.getStuName())
                .phone(student.getPhone())
                .college(student.getCollege())
                .adminClassName(student.getAdminClassName())
                .classId(mapping == null ? null : mapping.getClassId())
                .className(classInfo == null ? null : classInfo.getClassName())
                .teachingClassName(mapping == null ? null : mapping.getTeachingClassName())
                .groupId(mapping == null ? null : mapping.getGroupId())
                .groupName(group == null ? null : group.getGroupName())
                .status(student.getStatus())
                .build();
    }

    private void validateStuId(Long stuId) {
        if (stuId == null || !String.valueOf(stuId).matches("\\d{10}")) {
            throw new BusinessException("学号必须为10位数字");
        }
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
