package com.zjgs.equip.greatwork.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zjgs.equip.clazz.entity.ClassGroup;
import com.zjgs.equip.clazz.entity.ClassInfo;
import com.zjgs.equip.clazz.mapper.ClassGroupMapper;
import com.zjgs.equip.clazz.support.ClassGuard;
import com.zjgs.equip.common.BusinessException;
import com.zjgs.equip.common.ClassType;
import com.zjgs.equip.common.ErrorCode;
import com.zjgs.equip.common.SubmitStatus;
import com.zjgs.equip.greatwork.dto.GreatWorkCreateRequest;
import com.zjgs.equip.greatwork.dto.GreatWorkDetailVO;
import com.zjgs.equip.greatwork.dto.GreatWorkUpdateRequest;
import com.zjgs.equip.greatwork.dto.GreatWorkVO;
import com.zjgs.equip.greatwork.entity.GreatWork;
import com.zjgs.equip.greatwork.mapper.GreatWorkMapper;
import com.zjgs.equip.homework.entity.StuWork;
import com.zjgs.equip.homework.entity.TeacherWork;
import com.zjgs.equip.homework.mapper.StuWorkMapper;
import com.zjgs.equip.homework.mapper.TeacherWorkMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 优秀作业展示业务逻辑。
 */
@Service
@RequiredArgsConstructor
public class GreatWorkService {
    private final GreatWorkMapper greatWorkMapper;
    private final StuWorkMapper stuWorkMapper;
    private final TeacherWorkMapper teacherWorkMapper;
    private final ClassGroupMapper classGroupMapper;
    private final ClassGuard classGuard;

    public List<GreatWorkVO> list(Integer year, Integer classType) {
        if (classType != null) {
            ClassType.fromCode(classType);
        }
        LambdaQueryWrapper<GreatWork> wrapper = new LambdaQueryWrapper<>();
        if (year != null) {
            wrapper.eq(GreatWork::getYear, year);
        }
        if (classType != null) {
            wrapper.eq(GreatWork::getClassType, classType);
        }
        wrapper.orderByDesc(GreatWork::getCreatedAt).orderByDesc(GreatWork::getId);
        List<GreatWorkVO> result = new ArrayList<>();
        for (GreatWork greatWork : greatWorkMapper.selectList(wrapper)) {
            result.add(toVO(greatWork));
        }
        return result;
    }

    public GreatWorkDetailVO get(Long id) {
        GreatWork greatWork = requireGreatWork(id);
        return toDetail(greatWork);
    }

    @Transactional
    public GreatWorkVO create(Long stuWorkId, GreatWorkCreateRequest request, Long teacherId) {
        if (stuWorkId == null) {
            throw new BusinessException("提交ID不能为空");
        }
        StuWork submission = stuWorkMapper.selectById(stuWorkId);
        if (submission == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "作业提交不存在");
        }
        TeacherWork work = requireWork(submission.getTeacherWorkId());
        ClassInfo classInfo = classGuard.requireOwnedClass(work.getClassId(), teacherId);

        boolean graded = Objects.equals(submission.getSubmitStatus(), SubmitStatus.GRADED.getCode());
        if (submission.getScore() == null && !graded) {
            throw new BusinessException("仅已批改或已评分的作业可加入优秀作品");
        }
        if (greatWorkMapper.selectCount(new LambdaQueryWrapper<GreatWork>()
                .eq(GreatWork::getStuWorkId, stuWorkId)) > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "该作业已加入优秀作品");
        }

        int classType = request.getClassType() == null ? classInfo.getClassType() : request.getClassType();
        ClassType.fromCode(classType);
        int year = request.getYear() == null ? classInfo.getYear() : request.getYear();

        GreatWork greatWork = new GreatWork();
        greatWork.setStuWorkId(stuWorkId);
        greatWork.setWorkName(request.getWorkName().trim());
        greatWork.setPhotoUrl(request.getPhotoUrl());
        greatWork.setClassType(classType);
        greatWork.setYear(year);
        LocalDateTime now = LocalDateTime.now();
        greatWork.setCreatedAt(now);
        greatWork.setUpdatedAt(now);
        greatWorkMapper.insert(greatWork);
        return toVO(greatWorkMapper.selectById(greatWork.getId()));
    }

    @Transactional
    public GreatWorkVO update(Long id, GreatWorkUpdateRequest request, Long teacherId) {
        requireOwned(id, teacherId);
        LambdaUpdateWrapper<GreatWork> update = new LambdaUpdateWrapper<GreatWork>()
                .eq(GreatWork::getId, id)
                .set(GreatWork::getWorkName, request.getWorkName().trim())
                .set(GreatWork::getPhotoUrl, request.getPhotoUrl())
                .set(GreatWork::getUpdatedAt, LocalDateTime.now());
        if (request.getClassType() != null) {
            ClassType.fromCode(request.getClassType());
            update.set(GreatWork::getClassType, request.getClassType());
        }
        greatWorkMapper.update(null, update);
        return toVO(greatWorkMapper.selectById(id));
    }

    @Transactional
    public void delete(Long id, Long teacherId) {
        requireOwned(id, teacherId);
        greatWorkMapper.deleteById(id);
    }

    private OwnedChain requireOwned(Long id, Long teacherId) {
        GreatWork greatWork = requireGreatWork(id);
        StuWork submission = stuWorkMapper.selectById(greatWork.getStuWorkId());
        if (submission == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "关联的作业提交不存在");
        }
        TeacherWork work = requireWork(submission.getTeacherWorkId());
        ClassInfo classInfo = classGuard.requireOwnedClass(work.getClassId(), teacherId);
        return new OwnedChain(greatWork, submission, work, classInfo);
    }

    private GreatWork requireGreatWork(Long id) {
        if (id == null) {
            throw new BusinessException("优秀作品ID不能为空");
        }
        GreatWork greatWork = greatWorkMapper.selectById(id);
        if (greatWork == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "优秀作品不存在");
        }
        return greatWork;
    }

    private TeacherWork requireWork(Long workId) {
        TeacherWork work = teacherWorkMapper.selectById(workId);
        if (work == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "作业不存在");
        }
        return work;
    }

    private GreatWorkVO toVO(GreatWork greatWork) {
        StuWork submission = stuWorkMapper.selectById(greatWork.getStuWorkId());
        ClassGroup group = submission == null ? null : classGroupMapper.selectById(submission.getGroupId());
        return GreatWorkVO.builder()
                .id(greatWork.getId())
                .stuWorkId(greatWork.getStuWorkId())
                .workName(greatWork.getWorkName())
                .photoUrl(greatWork.getPhotoUrl())
                .classType(greatWork.getClassType())
                .year(greatWork.getYear())
                .members(group == null ? null : group.getMembersText())
                .createdAt(greatWork.getCreatedAt())
                .build();
    }

    private GreatWorkDetailVO toDetail(GreatWork greatWork) {
        StuWork submission = stuWorkMapper.selectById(greatWork.getStuWorkId());
        ClassGroup group = submission == null ? null : classGroupMapper.selectById(submission.getGroupId());
        TeacherWork work = submission == null ? null : teacherWorkMapper.selectById(submission.getTeacherWorkId());
        ClassInfo classInfo = work == null ? null : classGuard.requireClass(work.getClassId());

        GreatWorkDetailVO detail = new GreatWorkDetailVO();
        detail.setId(greatWork.getId());
        detail.setStuWorkId(greatWork.getStuWorkId());
        detail.setWorkName(greatWork.getWorkName());
        detail.setPhotoUrl(greatWork.getPhotoUrl());
        detail.setClassType(greatWork.getClassType());
        detail.setYear(greatWork.getYear());
        detail.setMembers(group == null ? null : group.getMembersText());
        detail.setCreatedAt(greatWork.getCreatedAt());
        detail.setWorkDoc(submission == null ? null : submission.getWorkDoc());
        detail.setVideoUrl(submission == null ? null : submission.getVideoUrl());
        detail.setZipUrl(submission == null ? null : submission.getZipUrl());
        detail.setScore(submission == null ? null : submission.getScore());
        detail.setClassName(classInfo == null ? null : classInfo.getClassName());
        detail.setGroupName(group == null ? null : group.getGroupName());
        detail.setGroupNum(group == null ? null : group.getGroupNum());
        detail.setSubmittedAt(submission == null ? null : submission.getSubmittedAt());
        return detail;
    }

    private record OwnedChain(GreatWork greatWork, StuWork submission, TeacherWork work, ClassInfo classInfo) {
    }
}
