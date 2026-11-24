package com.zjgs.equip.clazz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zjgs.equip.clazz.dto.ClassRequest;
import com.zjgs.equip.clazz.dto.ClassVO;
import com.zjgs.equip.clazz.entity.ClassGroup;
import com.zjgs.equip.clazz.entity.ClassInfo;
import com.zjgs.equip.clazz.entity.StudentClass;
import com.zjgs.equip.clazz.mapper.ClassGroupMapper;
import com.zjgs.equip.clazz.mapper.ClassInfoMapper;
import com.zjgs.equip.clazz.mapper.StudentClassMapper;
import com.zjgs.equip.clazz.support.ClassGuard;
import com.zjgs.equip.common.BusinessException;
import com.zjgs.equip.common.ClassType;
import com.zjgs.equip.common.ErrorCode;
import com.zjgs.equip.homework.entity.TeacherWork;
import com.zjgs.equip.homework.mapper.TeacherWorkMapper;
import com.zjgs.equip.user.entity.Teacher;
import com.zjgs.equip.user.mapper.TeacherMapper;
import com.zjgs.equip.ws.RealtimePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教学班级业务逻辑。
 */
@Service
@RequiredArgsConstructor
public class ClassService {
    private final ClassInfoMapper classInfoMapper;
    private final ClassGroupMapper classGroupMapper;
    private final StudentClassMapper studentClassMapper;
    private final TeacherWorkMapper teacherWorkMapper;
    private final TeacherMapper teacherMapper;
    private final ClassGuard classGuard;
    private final RealtimePublisher realtimePublisher;

    public List<ClassVO> list(String type, Integer year) {
        LambdaQueryWrapper<ClassInfo> wrapper = new LambdaQueryWrapper<>();
        if (type != null && !type.isBlank()) {
            wrapper.eq(ClassInfo::getClassType, parseClassType(type));
        }
        if (year != null) {
            wrapper.eq(ClassInfo::getYear, year);
        }
        wrapper.orderByDesc(ClassInfo::getYear).orderByAsc(ClassInfo::getClassName);
        List<ClassInfo> classes = classInfoMapper.selectList(wrapper);
        return toVOs(classes);
    }

    public ClassVO get(Long id) {
        return toVO(classGuard.requireClass(id));
    }

    @Transactional
    public ClassVO create(ClassRequest request, Long teacherId) {
        validateRequest(request, null);
        ClassInfo entity = new ClassInfo();
        entity.setClassName(request.getClassName().trim());
        entity.setClassType(request.getClassType());
        entity.setYear(request.getYear());
        entity.setStartTime(request.getStartTime());
        entity.setEndTime(request.getEndTime());
        entity.setTeacherId(teacherId);
        entity.setStatus(1);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        classInfoMapper.insert(entity);

        ClassInfo saved = classInfoMapper.selectById(entity.getId());
        realtimePublisher.publishClassEvent(saved.getId(), "class.updated", Map.of("classId", saved.getId()));
        return toVO(saved);
    }

    @Transactional
    public ClassVO update(Long id, ClassRequest request, Long teacherId) {
        classGuard.requireOwnedClass(id, teacherId);
        validateRequest(request, id);
        classInfoMapper.update(null, new LambdaUpdateWrapper<ClassInfo>()
                .eq(ClassInfo::getId, id)
                .set(ClassInfo::getClassName, request.getClassName().trim())
                .set(ClassInfo::getClassType, request.getClassType())
                .set(ClassInfo::getYear, request.getYear())
                .set(ClassInfo::getStartTime, request.getStartTime())
                .set(ClassInfo::getEndTime, request.getEndTime())
                .set(ClassInfo::getUpdatedAt, LocalDateTime.now()));

        ClassInfo saved = classInfoMapper.selectById(id);
        realtimePublisher.publishClassEvent(id, "class.updated", Map.of("classId", id));
        return toVO(saved);
    }

    @Transactional
    public void delete(Long id, Long teacherId) {
        classGuard.requireOwnedClass(id, teacherId);
        classInfoMapper.deleteById(id);
        realtimePublisher.publishClassEvent(id, "class.deleted", Map.of("classId", id));
    }

    /** 供其他模块复用，构建班级视图。 */
    public ClassVO toVO(ClassInfo entity) {
        return toVOs(List.of(entity)).get(0);
    }

    public List<ClassVO> toVOs(Collection<ClassInfo> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> teacherIds = entities.stream()
                .map(ClassInfo::getTeacherId).filter(java.util.Objects::nonNull).distinct().toList();
        Map<Long, String> teacherNames = new HashMap<>();
        if (!teacherIds.isEmpty()) {
            for (Teacher teacher : teacherMapper.selectBatchIds(teacherIds)) {
                teacherNames.put(teacher.getId(), teacher.getName());
            }
        }

        List<ClassVO> result = new ArrayList<>(entities.size());
        for (ClassInfo entity : entities) {
            long studentCount = studentClassMapper.selectCount(new LambdaQueryWrapper<StudentClass>()
                    .eq(StudentClass::getClassId, entity.getId()));
            long groupCount = classGroupMapper.selectCount(new LambdaQueryWrapper<ClassGroup>()
                    .eq(ClassGroup::getClassId, entity.getId()));
            long workCount = teacherWorkMapper.selectCount(new LambdaQueryWrapper<TeacherWork>()
                    .eq(TeacherWork::getClassId, entity.getId()));
            ClassType classType = entity.getClassType() == null ? null : ClassType.fromCode(entity.getClassType());
            result.add(ClassVO.builder()
                    .id(entity.getId())
                    .className(entity.getClassName())
                    .classType(entity.getClassType())
                    .classTypeName(classType == null ? null : classType.getLabel())
                    .year(entity.getYear())
                    .startTime(entity.getStartTime())
                    .endTime(entity.getEndTime())
                    .teacherId(entity.getTeacherId())
                    .teacherName(entity.getTeacherId() == null ? null : teacherNames.get(entity.getTeacherId()))
                    .studentCount((int) studentCount)
                    .groupCount((int) groupCount)
                    .workCount((int) workCount)
                    .status(entity.getStatus())
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .build());
        }
        return result;
    }

    private void validateRequest(ClassRequest request, Long excludeId) {
        if (request.getStartTime() != null && request.getEndTime() != null
                && request.getStartTime().isAfter(request.getEndTime())) {
            throw new BusinessException("开始日期不能晚于结束日期");
        }
        if (request.getClassType() != null) {
            // 未知类型会抛出 BusinessException
            ClassType.fromCode(request.getClassType());
        }
        String className = request.getClassName().trim();
        LambdaQueryWrapper<ClassInfo> wrapper = new LambdaQueryWrapper<ClassInfo>()
                .eq(ClassInfo::getClassName, className)
                .eq(ClassInfo::getYear, request.getYear());
        if (excludeId != null) {
            wrapper.ne(ClassInfo::getId, excludeId);
        }
        if (classInfoMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "同一年份下班级名称已存在");
        }
    }

    private int parseClassType(String type) {
        String normalized = type.trim().toUpperCase();
        if ("ARM".equals(normalized)) {
            return ClassType.ARM.getCode();
        }
        if ("IOT".equals(normalized)) {
            return ClassType.IOT.getCode();
        }
        try {
            int code = Integer.parseInt(normalized);
            ClassType.fromCode(code);
            return code;
        } catch (NumberFormatException e) {
            throw new BusinessException("班级类型仅支持 ARM 或 IOT");
        }
    }
}
