package com.zjgs.equip.clazz.support;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zjgs.equip.auth.UserPrincipal;
import com.zjgs.equip.clazz.entity.ClassGroup;
import com.zjgs.equip.clazz.entity.ClassInfo;
import com.zjgs.equip.clazz.entity.StudentClass;
import com.zjgs.equip.clazz.mapper.ClassGroupMapper;
import com.zjgs.equip.clazz.mapper.ClassInfoMapper;
import com.zjgs.equip.clazz.mapper.StudentClassMapper;
import com.zjgs.equip.common.BusinessException;
import com.zjgs.equip.common.ErrorCode;
import com.zjgs.equip.common.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 班级/小组访问与归属校验的公共助手。
 */
@Component
@RequiredArgsConstructor
public class ClassGuard {
    private final ClassInfoMapper classInfoMapper;
    private final ClassGroupMapper classGroupMapper;
    private final StudentClassMapper studentClassMapper;

    public ClassInfo requireClass(Long classId) {
        if (classId == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "班级ID不能为空");
        }
        ClassInfo classInfo = classInfoMapper.selectById(classId);
        if (classInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "班级不存在");
        }
        return classInfo;
    }

    /** 校验班级存在且属于当前教师。 */
    public ClassInfo requireOwnedClass(Long classId, Long teacherId) {
        ClassInfo classInfo = requireClass(classId);
        if (teacherId == null || !teacherId.equals(classInfo.getTeacherId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权操作该班级");
        }
        return classInfo;
    }

    public ClassGroup requireGroup(Long groupId) {
        if (groupId == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "小组ID不能为空");
        }
        ClassGroup group = classGroupMapper.selectById(groupId);
        if (group == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "小组不存在");
        }
        return group;
    }

    public ClassGroup requireOwnedGroup(Long groupId, Long teacherId) {
        ClassGroup group = requireGroup(groupId);
        requireOwnedClass(group.getClassId(), teacherId);
        return group;
    }

    /** 学生最新的一条班级映射。 */
    public StudentClass latestStudentClass(Long stuId) {
        if (stuId == null) {
            return null;
        }
        return studentClassMapper.selectOne(new LambdaQueryWrapper<StudentClass>()
                .eq(StudentClass::getStuId, stuId)
                .orderByDesc(StudentClass::getJoinedAt)
                .orderByDesc(StudentClass::getId)
                .last("limit 1"));
    }

    public StudentClass studentClassOf(Long stuId, Long classId) {
        if (stuId == null || classId == null) {
            return null;
        }
        return studentClassMapper.selectOne(new LambdaQueryWrapper<StudentClass>()
                .eq(StudentClass::getStuId, stuId)
                .eq(StudentClass::getClassId, classId)
                .last("limit 1"));
    }

    /** 教师可访问任意学生；学生只能访问自己；访客拒绝。 */
    public void assertTeacherOrSelf(UserPrincipal principal, Long stuId) {
        if (principal == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录或登录已过期");
        }
        if (principal.role() == Role.TEACHER) {
            return;
        }
        if (principal.role() == Role.STUDENT && stuId != null && stuId.equals(principal.stuId())) {
            return;
        }
        throw new BusinessException(ErrorCode.FORBIDDEN, "无权访问其他学生的信息");
    }
}
