package com.zjgs.equip.stats;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zjgs.equip.clazz.entity.ClassGroup;
import com.zjgs.equip.clazz.entity.ClassInfo;
import com.zjgs.equip.clazz.entity.StudentClass;
import com.zjgs.equip.clazz.mapper.ClassGroupMapper;
import com.zjgs.equip.clazz.mapper.ClassInfoMapper;
import com.zjgs.equip.clazz.mapper.StudentClassMapper;
import com.zjgs.equip.common.BusinessException;
import com.zjgs.equip.common.ErrorCode;
import com.zjgs.equip.equipment.entity.Equipment;
import com.zjgs.equip.equipment.entity.EquipmentBorrow;
import com.zjgs.equip.equipment.entity.LabUsageLog;
import com.zjgs.equip.equipment.mapper.EquipmentBorrowMapper;
import com.zjgs.equip.equipment.mapper.EquipmentMapper;
import com.zjgs.equip.equipment.mapper.LabUsageLogMapper;
import com.zjgs.equip.homework.entity.StuWork;
import com.zjgs.equip.homework.entity.TeacherWork;
import com.zjgs.equip.homework.mapper.StuWorkMapper;
import com.zjgs.equip.homework.mapper.TeacherWorkMapper;
import com.zjgs.equip.stats.dto.AttendanceStatVO;
import com.zjgs.equip.stats.dto.ClassStatsVO;
import com.zjgs.equip.stats.dto.EquipmentStatsVO;
import com.zjgs.equip.user.entity.Student;
import com.zjgs.equip.user.mapper.StudentMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 统计报表，直接使用 Mapper 查询，不依赖 academic 服务。
 */
@Service
public class StatsService {
    private final ClassInfoMapper classInfoMapper;
    private final ClassGroupMapper classGroupMapper;
    private final StudentClassMapper studentClassMapper;
    private final StudentMapper studentMapper;
    private final TeacherWorkMapper teacherWorkMapper;
    private final StuWorkMapper stuWorkMapper;
    private final EquipmentMapper equipmentMapper;
    private final EquipmentBorrowMapper equipmentBorrowMapper;
    private final LabUsageLogMapper labUsageLogMapper;

    public StatsService(ClassInfoMapper classInfoMapper,
                        ClassGroupMapper classGroupMapper,
                        StudentClassMapper studentClassMapper,
                        StudentMapper studentMapper,
                        TeacherWorkMapper teacherWorkMapper,
                        StuWorkMapper stuWorkMapper,
                        EquipmentMapper equipmentMapper,
                        EquipmentBorrowMapper equipmentBorrowMapper,
                        LabUsageLogMapper labUsageLogMapper) {
        this.classInfoMapper = classInfoMapper;
        this.classGroupMapper = classGroupMapper;
        this.studentClassMapper = studentClassMapper;
        this.studentMapper = studentMapper;
        this.teacherWorkMapper = teacherWorkMapper;
        this.stuWorkMapper = stuWorkMapper;
        this.equipmentMapper = equipmentMapper;
        this.equipmentBorrowMapper = equipmentBorrowMapper;
        this.labUsageLogMapper = labUsageLogMapper;
    }

    public ClassStatsVO classStats(Long classId) {
        ClassInfo classInfo = classInfoMapper.selectById(classId);
        if (classInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "班级不存在");
        }
        long studentCount = studentClassMapper.selectCount(new LambdaQueryWrapper<StudentClass>()
                .eq(StudentClass::getClassId, classId));
        long groupCount = classGroupMapper.selectCount(new LambdaQueryWrapper<ClassGroup>()
                .eq(ClassGroup::getClassId, classId));

        List<TeacherWork> works = teacherWorkMapper.selectList(new LambdaQueryWrapper<TeacherWork>()
                .eq(TeacherWork::getClassId, classId)
                .orderByAsc(TeacherWork::getStartTime)
                .orderByAsc(TeacherWork::getId));
        long workCount = works.size();

        List<StuWork> submissions = Collections.emptyList();
        if (!works.isEmpty()) {
            List<Long> workIds = works.stream().map(TeacherWork::getId).toList();
            submissions = stuWorkMapper.selectList(new LambdaQueryWrapper<StuWork>()
                    .in(StuWork::getTeacherWorkId, workIds));
        }
        long submissionCount = submissions.size();
        long gradedCount = submissions.stream()
                .filter(work -> Objects.equals(work.getSubmitStatus(), 3))
                .count();
        List<BigDecimal> scores = submissions.stream()
                .map(StuWork::getScore)
                .filter(Objects::nonNull)
                .toList();
        BigDecimal averageScore = scores.isEmpty()
                ? BigDecimal.ZERO
                : scores.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(scores.size()), 2, RoundingMode.HALF_UP);

        long attendanceCount = labUsageLogMapper.selectCount(new LambdaQueryWrapper<LabUsageLog>()
                .eq(LabUsageLog::getClassId, classId)
                .eq(LabUsageLog::getEventType, "BORROW"));

        return ClassStatsVO.builder()
                .classId(classId)
                .className(classInfo.getClassName())
                .studentCount(studentCount)
                .groupCount(groupCount)
                .workCount(workCount)
                .submissionCount(submissionCount)
                .gradedCount(gradedCount)
                .averageScore(averageScore)
                .attendanceCount(attendanceCount)
                .build();
    }

    public EquipmentStatsVO equipmentStats() {
        List<Equipment> equipments = equipmentMapper.selectList(null);
        long totalStock = 0;
        long usedStock = 0;
        for (Equipment equipment : equipments) {
            totalStock += equipment.getTotalNumber() == null ? 0 : equipment.getTotalNumber();
            usedStock += equipment.getUsedNumber() == null ? 0 : equipment.getUsedNumber();
        }
        long applying = equipmentBorrowMapper.selectCount(new LambdaQueryWrapper<EquipmentBorrow>()
                .eq(EquipmentBorrow::getStatus, 0));
        long borrowed = equipmentBorrowMapper.selectCount(new LambdaQueryWrapper<EquipmentBorrow>()
                .eq(EquipmentBorrow::getStatus, 1));
        return EquipmentStatsVO.builder()
                .totalEquipmentTypes(equipments.size())
                .totalStock(totalStock)
                .usedStock(usedStock)
                .availableStock(Math.max(totalStock - usedStock, 0))
                .applyingBorrowCount(applying)
                .borrowedCount(borrowed)
                .build();
    }

    public List<AttendanceStatVO> attendance(Long classId, LocalDate from, LocalDate to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new BusinessException("开始日期不能晚于结束日期");
        }
        LambdaQueryWrapper<LabUsageLog> wrapper = new LambdaQueryWrapper<LabUsageLog>()
                .eq(LabUsageLog::getEventType, "BORROW")
                .eq(classId != null, LabUsageLog::getClassId, classId)
                .ge(from != null, LabUsageLog::getOccurredAt, from == null ? null : from.atStartOfDay())
                .le(to != null, LabUsageLog::getOccurredAt, to == null ? null : to.atTime(23, 59, 59))
                .orderByAsc(LabUsageLog::getOccurredAt);
        List<LabUsageLog> logs = labUsageLogMapper.selectList(wrapper);
        if (logs.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, List<LabUsageLog>> byStudent = logs.stream()
                .collect(Collectors.groupingBy(LabUsageLog::getStuId, LinkedHashMap::new, Collectors.toList()));
        List<Long> stuIds = byStudent.keySet().stream().toList();
        Map<Long, Student> students = studentMapper.selectBatchIds(stuIds).stream()
                .collect(Collectors.toMap(Student::getStuId, Function.identity()));

        Map<Long, String> classNames = new LinkedHashMap<>();
        if (classId != null) {
            ClassInfo info = classInfoMapper.selectById(classId);
            if (info != null) {
                classNames.put(classId, info.getClassName());
            }
        } else {
            List<Long> classIds = logs.stream().map(LabUsageLog::getClassId).filter(Objects::nonNull)
                    .distinct().toList();
            if (!classIds.isEmpty()) {
                classInfoMapper.selectBatchIds(classIds)
                        .forEach(info -> classNames.put(info.getId(), info.getClassName()));
            }
        }

        return byStudent.entrySet().stream()
                .map(entry -> {
                    List<LabUsageLog> studentLogs = entry.getValue();
                    LabUsageLog last = studentLogs.get(studentLogs.size() - 1);
                    Student student = students.get(entry.getKey());
                    String className = last.getClassId() == null ? null : classNames.get(last.getClassId());
                    if (className == null && student != null) {
                        className = student.getAdminClassName();
                    }
                    return AttendanceStatVO.builder()
                            .stuId(entry.getKey())
                            .stuName(student == null ? null : student.getStuName())
                            .className(className)
                            .borrowCount(studentLogs.size())
                            .lastBorrowAt(last.getOccurredAt())
                            .build();
                })
                .sorted(Comparator.comparing(AttendanceStatVO::getLastBorrowAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }
}
