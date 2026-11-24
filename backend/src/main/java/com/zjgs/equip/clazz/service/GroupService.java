package com.zjgs.equip.clazz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zjgs.equip.clazz.dto.GroupRequest;
import com.zjgs.equip.clazz.dto.GroupVO;
import com.zjgs.equip.clazz.entity.ClassGroup;
import com.zjgs.equip.clazz.entity.ClassInfo;
import com.zjgs.equip.clazz.entity.StudentClass;
import com.zjgs.equip.clazz.mapper.ClassGroupMapper;
import com.zjgs.equip.clazz.mapper.ClassInfoMapper;
import com.zjgs.equip.clazz.mapper.StudentClassMapper;
import com.zjgs.equip.clazz.support.ClassGuard;
import com.zjgs.equip.common.BusinessException;
import com.zjgs.equip.common.ErrorCode;
import com.zjgs.equip.user.entity.Student;
import com.zjgs.equip.user.mapper.StudentMapper;
import com.zjgs.equip.ws.RealtimePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 班级小组业务逻辑。
 */
@Service
@RequiredArgsConstructor
public class GroupService {
    private final ClassGroupMapper classGroupMapper;
    private final ClassInfoMapper classInfoMapper;
    private final StudentClassMapper studentClassMapper;
    private final StudentMapper studentMapper;
    private final ClassGuard classGuard;
    private final RealtimePublisher realtimePublisher;

    public List<GroupVO> listByClass(Long classId) {
        classGuard.requireClass(classId);
        List<ClassGroup> groups = classGroupMapper.selectList(new LambdaQueryWrapper<ClassGroup>()
                .eq(ClassGroup::getClassId, classId)
                .orderByAsc(ClassGroup::getId));
        return toVOs(groups);
    }

    public List<GroupVO> list(Long classId) {
        LambdaQueryWrapper<ClassGroup> wrapper = new LambdaQueryWrapper<>();
        if (classId != null) {
            classGuard.requireClass(classId);
            wrapper.eq(ClassGroup::getClassId, classId);
        }
        wrapper.orderByAsc(ClassGroup::getClassId).orderByAsc(ClassGroup::getId);
        return toVOs(classGroupMapper.selectList(wrapper));
    }

    public GroupVO get(Long id) {
        return toVO(classGuard.requireGroup(id));
    }

    @Transactional
    public GroupVO create(GroupRequest request, Long teacherId) {
        if (request.getClassId() == null) {
            throw new BusinessException("班级ID不能为空");
        }
        classGuard.requireOwnedClass(request.getClassId(), teacherId);
        String groupName = request.getGroupName().trim();
        ensureNameAvailable(request.getClassId(), groupName, null);

        ClassGroup group = new ClassGroup();
        group.setClassId(request.getClassId());
        group.setGroupName(groupName);
        group.setGroupNum(0);
        group.setTeachingClassName(blankToNull(request.getTeachingClassName()));
        LocalDateTime now = LocalDateTime.now();
        group.setCreatedAt(now);
        group.setUpdatedAt(now);
        classGroupMapper.insert(group);

        if (request.getMemberStuIds() != null) {
            replaceMembers(group, request.getMemberStuIds());
        }
        recomputeGroup(group.getId());
        realtimePublisher.publishClassEvent(group.getClassId(), "group.updated",
                Map.of("groupId", group.getId(), "classId", group.getClassId()));
        return get(group.getId());
    }

    @Transactional
    public GroupVO update(Long id, GroupRequest request, Long teacherId) {
        ClassGroup group = classGuard.requireOwnedGroup(id, teacherId);
        if (request.getClassId() != null && !request.getClassId().equals(group.getClassId())) {
            throw new BusinessException("不允许修改小组所属班级");
        }
        String groupName = request.getGroupName().trim();
        ensureNameAvailable(group.getClassId(), groupName, id);

        boolean teachingClassChanged = !Objects.equals(blankToNull(request.getTeachingClassName()),
                group.getTeachingClassName());
        String teachingClassName = blankToNull(request.getTeachingClassName());
        group.setGroupName(groupName);
        group.setTeachingClassName(teachingClassName);
        classGroupMapper.update(null, new LambdaUpdateWrapper<ClassGroup>()
                .eq(ClassGroup::getId, id)
                .set(ClassGroup::getGroupName, groupName)
                .set(ClassGroup::getTeachingClassName, teachingClassName)
                .set(ClassGroup::getUpdatedAt, LocalDateTime.now()));

        if (teachingClassChanged && group.getTeachingClassName() != null) {
            studentClassMapper.update(null, new LambdaUpdateWrapper<StudentClass>()
                    .eq(StudentClass::getGroupId, group.getId())
                    .set(StudentClass::getTeachingClassName, group.getTeachingClassName()));
        }
        if (request.getMemberStuIds() != null) {
            replaceMembers(group, request.getMemberStuIds());
        }
        recomputeGroup(group.getId());
        realtimePublisher.publishClassEvent(group.getClassId(), "group.updated",
                Map.of("groupId", group.getId(), "classId", group.getClassId()));
        return get(group.getId());
    }

    @Transactional
    public void delete(Long id, Long teacherId) {
        ClassGroup group = classGuard.requireOwnedGroup(id, teacherId);
        clearGroupMembers(id);
        classGroupMapper.deleteById(id);
        realtimePublisher.publishClassEvent(group.getClassId(), "group.updated",
                Map.of("groupId", id, "classId", group.getClassId(), "deleted", true));
    }

    /** 删除班级下全部小组，先清空学生映射中的 group_id。 */
    @Transactional
    public void deleteByClass(Long classId, Long teacherId) {
        classGuard.requireOwnedClass(classId, teacherId);
        studentClassMapper.update(null, new LambdaUpdateWrapper<StudentClass>()
                .eq(StudentClass::getClassId, classId)
                .set(StudentClass::getGroupId, null));
        classGroupMapper.delete(new LambdaQueryWrapper<ClassGroup>().eq(ClassGroup::getClassId, classId));
        realtimePublisher.publishClassEvent(classId, "group.updated",
                Map.of("classId", classId, "deletedAll", true));
    }

    /** 教师分配单个学生到小组。 */
    @Transactional
    public GroupVO assignStudent(Long groupId, Long stuId, Long teacherId) {
        ClassGroup group = classGuard.requireOwnedGroup(groupId, teacherId);
        assignStudentToGroup(groupId, stuId);
        return get(group.getId());
    }

    /** 解除单个学生的小组分配。 */
    @Transactional
    public GroupVO unassignStudent(Long groupId, Long stuId, Long teacherId) {
        ClassGroup group = classGuard.requireOwnedGroup(groupId, teacherId);
        StudentClass mapping = classGuard.studentClassOf(stuId, group.getClassId());
        if (mapping == null || !Objects.equals(mapping.getGroupId(), groupId)) {
            throw new BusinessException("该学生不在当前小组中");
        }
        mapping.setGroupId(null);
        studentClassMapper.update(null, new LambdaUpdateWrapper<StudentClass>()
                .eq(StudentClass::getId, mapping.getId())
                .set(StudentClass::getGroupId, null));
        recomputeGroup(groupId);
        realtimePublisher.publishClassEvent(group.getClassId(), "group.updated",
                Map.of("groupId", groupId, "classId", group.getClassId()));
        return get(groupId);
    }

    /**
     * 内部分配（调用方需自行完成权限校验）。会同步重算新旧小组的成员文本。
     */
    @Transactional
    public void assignStudentToGroup(Long groupId, Long stuId) {
        ClassGroup group = classGuard.requireGroup(groupId);
        if (stuId == null) {
            throw new BusinessException("学号不能为空");
        }
        Student student = studentMapper.selectById(stuId);
        if (student == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "学生不存在: " + stuId);
        }
        StudentClass mapping = classGuard.studentClassOf(stuId, group.getClassId());
        if (mapping == null) {
            throw new BusinessException("学生 " + stuId + " 不属于该班级，请先添加学生");
        }
        Long oldGroupId = mapping.getGroupId();
        if (Objects.equals(oldGroupId, groupId)) {
            recomputeGroup(groupId);
            return;
        }
        mapping.setGroupId(groupId);
        if (group.getTeachingClassName() != null && !group.getTeachingClassName().isBlank()) {
            mapping.setTeachingClassName(group.getTeachingClassName());
        }
        studentClassMapper.updateById(mapping);

        recomputeGroup(groupId);
        if (oldGroupId != null) {
            recomputeGroup(oldGroupId);
        }
        realtimePublisher.publishClassEvent(group.getClassId(), "group.updated",
                Map.of("groupId", groupId, "classId", group.getClassId()));
    }

    /** 依据 student_class 重建 group_num / members_text。 */
    @Transactional
    public void recomputeGroup(Long groupId) {
        if (groupId == null) {
            return;
        }
        ClassGroup group = classGroupMapper.selectById(groupId);
        if (group == null) {
            return;
        }
        List<StudentClass> memberships = studentClassMapper.selectList(new LambdaQueryWrapper<StudentClass>()
                .eq(StudentClass::getGroupId, groupId)
                .orderByAsc(StudentClass::getStuId));
        List<Long> stuIds = memberships.stream().map(StudentClass::getStuId).toList();
        Map<Long, String> names = stuIds.isEmpty() ? Map.of()
                : studentMapper.selectBatchIds(stuIds).stream()
                .collect(Collectors.toMap(Student::getStuId, Student::getStuName));
        String membersText = memberships.stream()
                .map(m -> m.getStuId() + " " + names.getOrDefault(m.getStuId(), ""))
                .collect(Collectors.joining(", "));

        ClassGroup patch = new ClassGroup();
        patch.setId(groupId);
        patch.setGroupNum(memberships.size());
        patch.setMembersText(membersText.isBlank() ? null : membersText);
        patch.setUpdatedAt(LocalDateTime.now());
        // 显式 set，确保成员清空时 members_text 能被置空
        classGroupMapper.update(null, new LambdaUpdateWrapper<ClassGroup>()
                .eq(ClassGroup::getId, groupId)
                .set(ClassGroup::getGroupNum, patch.getGroupNum())
                .set(ClassGroup::getMembersText, patch.getMembersText())
                .set(ClassGroup::getUpdatedAt, patch.getUpdatedAt()));
    }

    /** Excel 导入用：按班级+名称查找或创建小组。 */
    @Transactional
    public ClassGroup findOrCreateGroup(Long classId, String groupName, String teachingClassName) {
        String name = groupName == null ? null : groupName.trim();
        if (name == null || name.isBlank()) {
            throw new BusinessException("小组名称不能为空");
        }
        ClassGroup group = classGroupMapper.selectOne(new LambdaQueryWrapper<ClassGroup>()
                .eq(ClassGroup::getClassId, classId)
                .eq(ClassGroup::getGroupName, name)
                .last("limit 1"));
        if (group != null) {
            if ((group.getTeachingClassName() == null || group.getTeachingClassName().isBlank())
                    && teachingClassName != null && !teachingClassName.isBlank()) {
                group.setTeachingClassName(teachingClassName.trim());
                group.setUpdatedAt(LocalDateTime.now());
                classGroupMapper.updateById(group);
            }
            return group;
        }
        group = new ClassGroup();
        group.setClassId(classId);
        group.setGroupName(name);
        group.setGroupNum(0);
        group.setTeachingClassName(blankToNull(teachingClassName));
        LocalDateTime now = LocalDateTime.now();
        group.setCreatedAt(now);
        group.setUpdatedAt(now);
        classGroupMapper.insert(group);
        return classGroupMapper.selectById(group.getId());
    }

    public GroupVO toVO(ClassGroup group) {
        return toVOs(List.of(group)).get(0);
    }

    public List<GroupVO> toVOs(List<ClassGroup> groups) {
        List<GroupVO> result = new ArrayList<>();
        if (groups == null || groups.isEmpty()) {
            return result;
        }
        Map<Long, String> classNames = classInfoMapper.selectBatchIds(
                        groups.stream().map(ClassGroup::getClassId).distinct().toList())
                .stream().collect(Collectors.toMap(ClassInfo::getId, ClassInfo::getClassName));
        for (ClassGroup group : groups) {
            List<StudentClass> memberships = studentClassMapper.selectList(new LambdaQueryWrapper<StudentClass>()
                    .eq(StudentClass::getGroupId, group.getId())
                    .orderByAsc(StudentClass::getStuId));
            List<Long> memberIds = memberships.stream().map(StudentClass::getStuId).toList();
            Map<Long, String> names = memberIds.isEmpty() ? Map.of()
                    : studentMapper.selectBatchIds(memberIds).stream()
                    .collect(Collectors.toMap(Student::getStuId, Student::getStuName));
            List<String> memberNames = memberIds.stream()
                    .map(id -> names.getOrDefault(id, ""))
                    .toList();
            String membersText = group.getMembersText();
            if ((membersText == null || membersText.isBlank()) && !memberIds.isEmpty()) {
                membersText = memberIds.stream()
                        .map(id -> id + " " + names.getOrDefault(id, ""))
                        .collect(Collectors.joining(", "));
            }
            result.add(GroupVO.builder()
                    .id(group.getId())
                    .classId(group.getClassId())
                    .className(classNames.get(group.getClassId()))
                    .groupName(group.getGroupName())
                    .groupNum(group.getGroupNum() == null ? memberIds.size() : group.getGroupNum())
                    .teachingClassName(group.getTeachingClassName())
                    .members(membersText)
                    .memberStuIds(memberIds)
                    .memberNames(memberNames)
                    .build());
        }
        return result;
    }

    /** 整体替换小组成员。 */
    private void replaceMembers(ClassGroup group, List<Long> memberStuIds) {
        Set<Long> targetIds = new LinkedHashSet<>();
        for (Long stuId : memberStuIds) {
            if (stuId != null) {
                targetIds.add(stuId);
            }
        }
        // 先校验，避免部分写入
        List<StudentClass> targetMappings = new ArrayList<>();
        for (Long stuId : targetIds) {
            StudentClass mapping = classGuard.studentClassOf(stuId, group.getClassId());
            if (mapping == null) {
                throw new BusinessException("学生 " + stuId + " 不属于该班级，不能分配到该小组");
            }
            targetMappings.add(mapping);
        }

        Set<Long> touchedGroups = new LinkedHashSet<>();
        List<StudentClass> currentMembers = studentClassMapper.selectList(new LambdaQueryWrapper<StudentClass>()
                .eq(StudentClass::getGroupId, group.getId()));
        for (StudentClass current : currentMembers) {
            if (!targetIds.contains(current.getStuId())) {
                studentClassMapper.update(null, new LambdaUpdateWrapper<StudentClass>()
                        .eq(StudentClass::getId, current.getId())
                        .set(StudentClass::getGroupId, null));
            }
        }
        for (StudentClass mapping : targetMappings) {
            if (mapping.getGroupId() != null && !Objects.equals(mapping.getGroupId(), group.getId())) {
                touchedGroups.add(mapping.getGroupId());
            }
            mapping.setGroupId(group.getId());
            if (group.getTeachingClassName() != null && !group.getTeachingClassName().isBlank()) {
                mapping.setTeachingClassName(group.getTeachingClassName());
            }
            studentClassMapper.updateById(mapping);
        }
        for (Long touched : touchedGroups) {
            recomputeGroup(touched);
        }
        recomputeGroup(group.getId());
    }

    private void clearGroupMembers(Long groupId) {
        studentClassMapper.update(null, new LambdaUpdateWrapper<StudentClass>()
                .eq(StudentClass::getGroupId, groupId)
                .set(StudentClass::getGroupId, null));
    }

    private void ensureNameAvailable(Long classId, String groupName, Long excludeId) {
        LambdaQueryWrapper<ClassGroup> wrapper = new LambdaQueryWrapper<ClassGroup>()
                .eq(ClassGroup::getClassId, classId)
                .eq(ClassGroup::getGroupName, groupName);
        if (excludeId != null) {
            wrapper.ne(ClassGroup::getId, excludeId);
        }
        if (classGroupMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "该班级下小组名称已存在");
        }
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
