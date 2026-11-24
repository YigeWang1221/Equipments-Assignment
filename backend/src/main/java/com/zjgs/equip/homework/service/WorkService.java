package com.zjgs.equip.homework.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zjgs.equip.clazz.entity.ClassGroup;
import com.zjgs.equip.clazz.entity.ClassInfo;
import com.zjgs.equip.clazz.mapper.ClassGroupMapper;
import com.zjgs.equip.clazz.mapper.ClassInfoMapper;
import com.zjgs.equip.clazz.support.ClassGuard;
import com.zjgs.equip.common.BusinessException;
import com.zjgs.equip.common.ErrorCode;
import com.zjgs.equip.common.WorkStatus;
import com.zjgs.equip.homework.dto.WorkContentVO;
import com.zjgs.equip.homework.dto.WorkDetailVO;
import com.zjgs.equip.homework.dto.WorkRequest;
import com.zjgs.equip.homework.dto.WorkVO;
import com.zjgs.equip.homework.entity.StuWork;
import com.zjgs.equip.homework.entity.TeacherWork;
import com.zjgs.equip.homework.mapper.StuWorkMapper;
import com.zjgs.equip.homework.mapper.TeacherWorkMapper;
import com.zjgs.equip.homework.support.FlexibleDateTime;
import com.zjgs.equip.homework.support.WorkStatusCalculator;
import com.zjgs.equip.ws.RealtimePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 教师作业业务逻辑。
 */
@Service
@RequiredArgsConstructor
public class WorkService {
    private final TeacherWorkMapper teacherWorkMapper;
    private final StuWorkMapper stuWorkMapper;
    private final ClassGroupMapper classGroupMapper;
    private final ClassInfoMapper classInfoMapper;
    private final ClassGuard classGuard;
    private final RealtimePublisher realtimePublisher;

    public List<WorkVO> list(Long classId, Integer status) {
        if (classId != null) {
            classGuard.requireClass(classId);
        }
        if (status != null) {
            if (status < WorkStatus.NOT_STARTED.getCode() || status > WorkStatus.ENDED.getCode()) {
                throw new BusinessException("作业状态非法");
            }
        }
        LambdaQueryWrapper<TeacherWork> wrapper = new LambdaQueryWrapper<>();
        if (classId != null) {
            wrapper.eq(TeacherWork::getClassId, classId);
        }
        wrapper.orderByDesc(TeacherWork::getCreatedAt).orderByDesc(TeacherWork::getId);
        List<TeacherWork> works = teacherWorkMapper.selectList(wrapper);
        List<WorkVO> result = new ArrayList<>();
        for (TeacherWork work : works) {
            WorkVO vo = buildVO(work, classInfoMapper.selectById(work.getClassId()));
            if (status == null || Objects.equals(status, vo.getStatus())) {
                result.add(vo);
            }
        }
        return result;
    }

    public WorkDetailVO get(Long id) {
        return toDetail(requireWork(id));
    }

    public TeacherWork requireWork(Long id) {
        if (id == null) {
            throw new BusinessException("作业ID不能为空");
        }
        TeacherWork work = teacherWorkMapper.selectById(id);
        if (work == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "作业不存在");
        }
        return work;
    }

    public WorkContentVO content(Long id) {
        TeacherWork work = requireWork(id);
        return WorkContentVO.builder()
                .contentHtml(work.getContentHtml())
                .contentJson(work.getContentJson())
                .version(work.getVersion())
                .updatedAt(work.getUpdatedAt())
                .build();
    }

    @Transactional
    public WorkDetailVO create(WorkRequest request, Long teacherId) {
        ClassInfo classInfo = classGuard.requireOwnedClass(request.getClassId(), teacherId);
        LocalDateTime startTime = FlexibleDateTime.parse(request.getStartTime());
        LocalDateTime endTime = FlexibleDateTime.parseEnd(request.getEndTime());
        validateTimes(startTime, endTime);

        TeacherWork work = new TeacherWork();
        work.setClassId(classInfo.getId());
        work.setWorkName(request.getWorkName().trim());
        work.setContentHtml(request.getContentHtml());
        work.setContentJson(request.getContentJson());
        work.setDetailUrl(request.getDetailUrl());
        work.setStartTime(startTime);
        work.setEndTime(endTime);
        work.setStatus(WorkStatusCalculator.calculate(startTime, endTime));
        work.setCreatedBy(teacherId);
        work.setVersion(0);
        LocalDateTime now = LocalDateTime.now();
        work.setCreatedAt(now);
        work.setUpdatedAt(now);
        teacherWorkMapper.insert(work);

        TeacherWork saved = teacherWorkMapper.selectById(work.getId());
        realtimePublisher.publishClassEvent(saved.getClassId(), "work.updated",
                Map.of("workId", saved.getId(), "classId", saved.getClassId(), "status", saved.getStatus()));
        return toDetail(saved);
    }

    @Transactional
    public WorkDetailVO update(Long id, WorkRequest request, Long teacherId) {
        TeacherWork work = requireWork(id);
        classGuard.requireOwnedClass(work.getClassId(), teacherId);
        if (request.getClassId() != null && !Objects.equals(request.getClassId(), work.getClassId())) {
            throw new BusinessException("不允许修改作业所属班级");
        }
        LocalDateTime startTime = FlexibleDateTime.parse(request.getStartTime());
        LocalDateTime endTime = FlexibleDateTime.parseEnd(request.getEndTime());
        validateTimes(startTime, endTime);

        int newVersion = (work.getVersion() == null ? 0 : work.getVersion()) + 1;
        teacherWorkMapper.update(null, new LambdaUpdateWrapper<TeacherWork>()
                .eq(TeacherWork::getId, id)
                .set(TeacherWork::getWorkName, request.getWorkName().trim())
                .set(TeacherWork::getContentHtml, request.getContentHtml())
                .set(TeacherWork::getContentJson, request.getContentJson())
                .set(TeacherWork::getDetailUrl, request.getDetailUrl())
                .set(TeacherWork::getStartTime, startTime)
                .set(TeacherWork::getEndTime, endTime)
                .set(TeacherWork::getStatus, WorkStatusCalculator.calculate(startTime, endTime))
                .set(TeacherWork::getVersion, newVersion)
                .set(TeacherWork::getUpdatedAt, LocalDateTime.now()));

        TeacherWork saved = teacherWorkMapper.selectById(id);
        realtimePublisher.publishClassEvent(saved.getClassId(), "work.updated",
                Map.of("workId", saved.getId(), "classId", saved.getClassId(), "status", saved.getStatus()));
        return toDetail(saved);
    }

    @Transactional
    public void delete(Long id, Long teacherId) {
        TeacherWork work = requireWork(id);
        classGuard.requireOwnedClass(work.getClassId(), teacherId);
        teacherWorkMapper.deleteById(id);
        realtimePublisher.publishClassEvent(work.getClassId(), "work.updated",
                Map.of("workId", id, "classId", work.getClassId(), "deleted", true));
    }

    public WorkVO buildVO(TeacherWork work, ClassInfo classInfo) {
        int status = WorkStatusCalculator.calculate(work.getStartTime(), work.getEndTime());
        long groupTotal = classGroupMapper.selectCount(
                new LambdaQueryWrapper<ClassGroup>()
                        .eq(ClassGroup::getClassId, work.getClassId()));
        long groupSubmitted = stuWorkMapper.selectCount(new LambdaQueryWrapper<StuWork>()
                .eq(StuWork::getTeacherWorkId, work.getId())
                .in(StuWork::getSubmitStatus, 2, 3));
        return WorkVO.builder()
                .id(work.getId())
                .classId(work.getClassId())
                .className(classInfo == null ? null : classInfo.getClassName())
                .workName(work.getWorkName())
                .status(status)
                .statusName(WorkStatusCalculator.statusName(status))
                .startTime(work.getStartTime())
                .endTime(work.getEndTime())
                .detailUrl(work.getDetailUrl())
                .groupTotal((int) groupTotal)
                .groupSubmitted((int) groupSubmitted)
                .createdAt(work.getCreatedAt())
                .updatedAt(work.getUpdatedAt())
                .build();
    }

    public WorkDetailVO toDetail(TeacherWork work) {
        WorkVO base = buildVO(work, classInfoMapper.selectById(work.getClassId()));
        WorkDetailVO detail = new WorkDetailVO();
        detail.setId(base.getId());
        detail.setClassId(base.getClassId());
        detail.setClassName(base.getClassName());
        detail.setWorkName(base.getWorkName());
        detail.setStatus(base.getStatus());
        detail.setStatusName(base.getStatusName());
        detail.setStartTime(base.getStartTime());
        detail.setEndTime(base.getEndTime());
        detail.setDetailUrl(base.getDetailUrl());
        detail.setGroupTotal(base.getGroupTotal());
        detail.setGroupSubmitted(base.getGroupSubmitted());
        detail.setCreatedAt(base.getCreatedAt());
        detail.setUpdatedAt(base.getUpdatedAt());
        detail.setContentHtml(work.getContentHtml());
        detail.setContentJson(work.getContentJson());
        return detail;
    }

    private void validateTimes(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            throw new BusinessException("开始时间不能晚于结束时间");
        }
    }
}
