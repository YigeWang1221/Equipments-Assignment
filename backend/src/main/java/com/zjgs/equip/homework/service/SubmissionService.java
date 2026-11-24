package com.zjgs.equip.homework.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zjgs.equip.auth.UserPrincipal;
import com.zjgs.equip.clazz.entity.ClassGroup;
import com.zjgs.equip.clazz.entity.ClassInfo;
import com.zjgs.equip.clazz.entity.StudentClass;
import com.zjgs.equip.clazz.mapper.ClassGroupMapper;
import com.zjgs.equip.clazz.mapper.ClassInfoMapper;
import com.zjgs.equip.clazz.mapper.StudentClassMapper;
import com.zjgs.equip.clazz.support.ClassGuard;
import com.zjgs.equip.common.BusinessException;
import com.zjgs.equip.common.ErrorCode;
import com.zjgs.equip.common.Role;
import com.zjgs.equip.common.SubmitStatus;
import com.zjgs.equip.common.WorkStatus;
import com.zjgs.equip.homework.dto.ScoreRequest;
import com.zjgs.equip.homework.dto.SubmissionDetailVO;
import com.zjgs.equip.homework.dto.SubmissionSummaryVO;
import com.zjgs.equip.homework.dto.SubmissionUpdateRequest;
import com.zjgs.equip.homework.entity.StuWork;
import com.zjgs.equip.homework.entity.TeacherWork;
import com.zjgs.equip.homework.mapper.StuWorkMapper;
import com.zjgs.equip.homework.mapper.TeacherWorkMapper;
import com.zjgs.equip.homework.support.WorkStatusCalculator;
import com.zjgs.equip.ws.RealtimePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 小组作业提交与评分业务逻辑。
 */
@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final StuWorkMapper stuWorkMapper;
    private final TeacherWorkMapper teacherWorkMapper;
    private final ClassGroupMapper classGroupMapper;
    private final ClassInfoMapper classInfoMapper;
    private final StudentClassMapper studentClassMapper;
    private final ClassGuard classGuard;
    private final RealtimePublisher realtimePublisher;

    /** 教师查看某次作业的小组提交列表。 */
    public List<SubmissionSummaryVO> listByWork(Long workId, Long teacherId) {
        TeacherWork work = requireWork(workId);
        classGuard.requireOwnedClass(work.getClassId(), teacherId);

        List<ClassGroup> groups = classGroupMapper.selectList(new LambdaQueryWrapper<ClassGroup>()
                .eq(ClassGroup::getClassId, work.getClassId())
                .orderByAsc(ClassGroup::getId));
        Map<Long, ClassGroup> groupMap = new HashMap<>();
        for (ClassGroup group : groups) {
            groupMap.put(group.getId(), group);
        }

        List<StuWork> submissions = stuWorkMapper.selectList(new LambdaQueryWrapper<StuWork>()
                .eq(StuWork::getTeacherWorkId, workId)
                .orderByAsc(StuWork::getGroupId));
        List<SubmissionSummaryVO> result = new ArrayList<>();
        for (StuWork submission : submissions) {
            result.add(toSummary(submission, groupMap.get(submission.getGroupId())));
        }
        return result;
    }

    /** 学生初始化（或获取）本小组的作业提交记录。 */
    @Transactional
    public SubmissionDetailVO init(Long workId, Long studentId) {
        TeacherWork work = requireWork(workId);
        ClassGroup group = requireStudentGroup(work.getClassId(), studentId);
        StuWork submission = findSubmission(workId, group.getId());
        if (submission == null) {
            LocalDateTime now = LocalDateTime.now();
            submission = new StuWork();
            submission.setTeacherWorkId(workId);
            submission.setGroupId(group.getId());
            submission.setSubmitStatus(SubmitStatus.NOT_STARTED.getCode());
            submission.setVersion(0);
            submission.setCreatedAt(now);
            submission.setUpdatedAt(now);
            stuWorkMapper.insert(submission);
            submission = stuWorkMapper.selectById(submission.getId());
            realtimePublisher.publishClassEvent(work.getClassId(), "submission.updated",
                    Map.of("submissionId", submission.getId(), "workId", workId, "groupId", group.getId()));
        }
        return toDetail(submission, work, group);
    }

    public SubmissionDetailVO mySubmission(Long workId, Long studentId) {
        TeacherWork work = requireWork(workId);
        ClassGroup group = requireStudentGroup(work.getClassId(), studentId);
        StuWork submission = findSubmission(workId, group.getId());
        if (submission == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "尚未初始化作业提交");
        }
        return toDetail(submission, work, group);
    }

    public SubmissionDetailVO get(Long id, UserPrincipal principal) {
        StuWork submission = requireSubmission(id);
        TeacherWork work = requireWork(submission.getTeacherWorkId());
        if (principal.role() == Role.TEACHER) {
            classGuard.requireOwnedClass(work.getClassId(), principal.userId());
        } else if (principal.role() == Role.STUDENT) {
            assertStudentOwnGroup(work, submission.getGroupId(), principal.stuId());
        } else {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权访问该作业提交");
        }
        return toDetail(submission, work, classGroupMapper.selectById(submission.getGroupId()));
    }

    @Transactional
    public SubmissionDetailVO update(Long id, SubmissionUpdateRequest request, Long studentId) {
        StuWork submission = requireSubmission(id);
        TeacherWork work = requireWork(submission.getTeacherWorkId());
        assertStudentOwnGroup(work, submission.getGroupId(), studentId);

        if (Objects.equals(submission.getSubmitStatus(), SubmitStatus.GRADED.getCode())) {
            throw new BusinessException(ErrorCode.CONFLICT, "作业已批改，不能再次修改");
        }
        boolean submit = Boolean.TRUE.equals(request.getSubmit());
        if (submit && WorkStatusCalculator.calculate(work.getStartTime(), work.getEndTime())
                == WorkStatus.ENDED.getCode()) {
            throw new BusinessException("作业已截止，无法提交");
        }

        LocalDateTime now = LocalDateTime.now();
        int status = submit ? SubmitStatus.SUBMITTED.getCode() : SubmitStatus.DRAFT.getCode();
        LocalDateTime submittedAt = submit ? now : submission.getSubmittedAt();
        int newVersion = (submission.getVersion() == null ? 0 : submission.getVersion()) + 1;
        stuWorkMapper.update(null, new LambdaUpdateWrapper<StuWork>()
                .eq(StuWork::getId, id)
                .set(StuWork::getWorkDoc, request.getWorkDoc())
                .set(StuWork::getVideoUrl, request.getVideoUrl())
                .set(StuWork::getZipUrl, request.getZipUrl())
                .set(StuWork::getSubmitStatus, status)
                .set(StuWork::getSubmittedAt, submittedAt)
                .set(StuWork::getVersion, newVersion)
                .set(StuWork::getUpdatedAt, now));

        StuWork saved = stuWorkMapper.selectById(id);
        realtimePublisher.publishClassEvent(work.getClassId(), "submission.updated",
                Map.of("submissionId", id, "workId", work.getId(), "groupId", saved.getGroupId(),
                        "status", saved.getSubmitStatus()));
        return toDetail(saved, work, classGroupMapper.selectById(saved.getGroupId()));
    }

    @Transactional
    public SubmissionDetailVO score(Long id, ScoreRequest request, Long teacherId) {
        StuWork submission = requireSubmission(id);
        TeacherWork work = requireWork(submission.getTeacherWorkId());
        classGuard.requireOwnedClass(work.getClassId(), teacherId);

        BigDecimal score = request.getScore();
        if (score == null || score.compareTo(BigDecimal.ZERO) < 0
                || score.compareTo(new BigDecimal("100")) > 0) {
            throw new BusinessException("分数必须在 0-100 之间");
        }
        submission.setScore(score.setScale(2, RoundingMode.HALF_UP));
        submission.setSubmitStatus(SubmitStatus.GRADED.getCode());
        if (submission.getSubmittedAt() == null) {
            submission.setSubmittedAt(LocalDateTime.now());
        }
        submission.setVersion((submission.getVersion() == null ? 0 : submission.getVersion()) + 1);
        submission.setUpdatedAt(LocalDateTime.now());
        stuWorkMapper.updateById(submission);

        StuWork saved = stuWorkMapper.selectById(id);
        realtimePublisher.publishClassEvent(work.getClassId(), "submission.updated",
                Map.of("submissionId", id, "workId", work.getId(), "groupId", saved.getGroupId(),
                        "status", saved.getSubmitStatus()));
        return toDetail(saved, work, classGroupMapper.selectById(saved.getGroupId()));
    }

    private TeacherWork requireWork(Long workId) {
        if (workId == null) {
            throw new BusinessException("作业ID不能为空");
        }
        TeacherWork work = teacherWorkMapper.selectById(workId);
        if (work == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "作业不存在");
        }
        return work;
    }

    private StuWork requireSubmission(Long id) {
        if (id == null) {
            throw new BusinessException("提交ID不能为空");
        }
        StuWork submission = stuWorkMapper.selectById(id);
        if (submission == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "作业提交不存在");
        }
        return submission;
    }

    private StuWork findSubmission(Long workId, Long groupId) {
        return stuWorkMapper.selectOne(new LambdaQueryWrapper<StuWork>()
                .eq(StuWork::getTeacherWorkId, workId)
                .eq(StuWork::getGroupId, groupId)
                .last("limit 1"));
    }

    private ClassGroup requireStudentGroup(Long classId, Long studentId) {
        StudentClass mapping = classGuard.studentClassOf(studentId, classId);
        if (mapping == null) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "当前学生不属于该作业班级");
        }
        if (mapping.getGroupId() == null) {
            throw new BusinessException("当前学生尚未分组，无法提交作业");
        }
        ClassGroup group = classGroupMapper.selectById(mapping.getGroupId());
        if (group == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "小组不存在");
        }
        return group;
    }

    private void assertStudentOwnGroup(TeacherWork work, Long groupId, Long studentId) {
        StudentClass mapping = classGuard.studentClassOf(studentId, work.getClassId());
        if (mapping == null || !Objects.equals(mapping.getGroupId(), groupId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只能操作本小组的作业提交");
        }
    }

    private SubmissionSummaryVO toSummary(StuWork submission, ClassGroup group) {
        SubmitStatus status = SubmitStatus.fromCode(submission.getSubmitStatus());
        return SubmissionSummaryVO.builder()
                .id(submission.getId())
                .teacherWorkId(submission.getTeacherWorkId())
                .groupId(submission.getGroupId())
                .groupName(group == null ? null : group.getGroupName())
                .groupNum(group == null ? null : group.getGroupNum())
                .members(group == null ? null : group.getMembersText())
                .score(submission.getScore())
                .submitStatus(status.getCode())
                .submitStatusName(status.getLabel())
                .submittedAt(submission.getSubmittedAt())
                .updatedAt(submission.getUpdatedAt())
                .build();
    }

    public SubmissionDetailVO toDetail(StuWork submission, TeacherWork work, ClassGroup group) {
        ClassInfo classInfo = classInfoMapper.selectById(work.getClassId());
        SubmitStatus status = SubmitStatus.fromCode(submission.getSubmitStatus());
        return SubmissionDetailVO.builder()
                .id(submission.getId())
                .teacherWorkId(work.getId())
                .workId(work.getId())
                .workName(work.getWorkName())
                .classId(work.getClassId())
                .className(classInfo == null ? null : classInfo.getClassName())
                .groupId(submission.getGroupId())
                .groupName(group == null ? null : group.getGroupName())
                .groupNum(group == null ? null : group.getGroupNum())
                .members(group == null ? null : group.getMembersText())
                .workDoc(submission.getWorkDoc())
                .videoUrl(submission.getVideoUrl())
                .zipUrl(submission.getZipUrl())
                .score(submission.getScore())
                .submitStatus(status.getCode())
                .submitStatusName(status.getLabel())
                .submittedAt(submission.getSubmittedAt())
                .updatedAt(submission.getUpdatedAt())
                .build();
    }
}
