package com.zjgs.equip.export;

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
import com.zjgs.equip.homework.entity.StuWork;
import com.zjgs.equip.homework.entity.TeacherWork;
import com.zjgs.equip.homework.mapper.StuWorkMapper;
import com.zjgs.equip.homework.mapper.TeacherWorkMapper;
import com.zjgs.equip.user.entity.Student;
import com.zjgs.equip.user.mapper.StudentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 导出服务：班级成绩单、单份作业 PDF、作业 PDF 打包。
 */
@Slf4j
@Service
public class ExportService {
    private static final String XLSX_CONTENT_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final ClassInfoMapper classInfoMapper;
    private final ClassGroupMapper classGroupMapper;
    private final StudentClassMapper studentClassMapper;
    private final StudentMapper studentMapper;
    private final TeacherWorkMapper teacherWorkMapper;
    private final StuWorkMapper stuWorkMapper;
    private final GradeExcelExporter gradeExcelExporter;
    private final PdfExportService pdfExportService;

    public ExportService(ClassInfoMapper classInfoMapper,
                         ClassGroupMapper classGroupMapper,
                         StudentClassMapper studentClassMapper,
                         StudentMapper studentMapper,
                         TeacherWorkMapper teacherWorkMapper,
                         StuWorkMapper stuWorkMapper,
                         GradeExcelExporter gradeExcelExporter,
                         PdfExportService pdfExportService) {
        this.classInfoMapper = classInfoMapper;
        this.classGroupMapper = classGroupMapper;
        this.studentClassMapper = studentClassMapper;
        this.studentMapper = studentMapper;
        this.teacherWorkMapper = teacherWorkMapper;
        this.stuWorkMapper = stuWorkMapper;
        this.gradeExcelExporter = gradeExcelExporter;
        this.pdfExportService = pdfExportService;
    }

    public ExportFile exportClassGrades(Long classId, UserPrincipal principal) {
        ClassInfo classInfo = requireOwnedClass(classId, principal);
        List<TeacherWork> works = teacherWorkMapper.selectList(new LambdaQueryWrapper<TeacherWork>()
                .eq(TeacherWork::getClassId, classId)
                .orderByAsc(TeacherWork::getStartTime)
                .orderByAsc(TeacherWork::getId));

        List<StudentClass> members = studentClassMapper.selectList(new LambdaQueryWrapper<StudentClass>()
                .eq(StudentClass::getClassId, classId));
        List<Long> stuIds = members.stream().map(StudentClass::getStuId).filter(Objects::nonNull)
                .distinct().toList();
        Map<Long, Student> students = stuIds.isEmpty() ? Map.of()
                : studentMapper.selectBatchIds(stuIds).stream()
                        .collect(Collectors.toMap(Student::getStuId, Function.identity()));

        List<Long> groupIds = members.stream().map(StudentClass::getGroupId).filter(Objects::nonNull)
                .distinct().toList();
        Map<Long, ClassGroup> groups = groupIds.isEmpty() ? Map.of()
                : classGroupMapper.selectBatchIds(groupIds).stream()
                        .collect(Collectors.toMap(ClassGroup::getId, Function.identity()));

        Map<String, BigDecimal> scoreByWorkAndGroup = new HashMap<>();
        if (!works.isEmpty()) {
            List<Long> workIds = works.stream().map(TeacherWork::getId).toList();
            stuWorkMapper.selectList(new LambdaQueryWrapper<StuWork>()
                            .in(StuWork::getTeacherWorkId, workIds))
                    .stream()
                    .filter(work -> work.getScore() != null)
                    .forEach(work -> scoreByWorkAndGroup.put(
                            scoreKey(work.getTeacherWorkId(), work.getGroupId()), work.getScore()));
        }

        List<GradeExcelExporter.GradeRow> rows = new ArrayList<>();
        for (StudentClass member : members.stream()
                .filter(item -> students.containsKey(item.getStuId()))
                .sorted(Comparator.comparing(StudentClass::getStuId))
                .toList()) {
            Student student = students.get(member.getStuId());
            ClassGroup group = member.getGroupId() == null ? null : groups.get(member.getGroupId());
            Map<Long, BigDecimal> scores = new HashMap<>();
            BigDecimal sum = BigDecimal.ZERO;
            int count = 0;
            for (TeacherWork work : works) {
                BigDecimal score = scoreByWorkAndGroup.get(scoreKey(work.getId(), member.getGroupId()));
                if (score != null) {
                    scores.put(work.getId(), score);
                    sum = sum.add(score);
                    count++;
                }
            }
            BigDecimal average = count == 0 ? null
                    : sum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
            rows.add(new GradeExcelExporter.GradeRow(member.getStuId(), student.getStuName(),
                    group == null ? "" : group.getGroupName(), scores, average));
        }

        byte[] content = gradeExcelExporter.export(classInfo.getClassName(), works, rows);
        String filename = safeFilename(classInfo.getClassName() + "-成绩单") + ".xlsx";
        return new ExportFile(content, filename, XLSX_CONTENT_TYPE);
    }

    public ExportFile exportSubmissionPdf(Long stuWorkId, UserPrincipal principal) {
        StuWork stuWork = stuWorkMapper.selectById(stuWorkId);
        if (stuWork == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "作业提交不存在");
        }
        TeacherWork work = teacherWorkMapper.selectById(stuWork.getTeacherWorkId());
        if (work == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "作业不存在");
        }
        ClassInfo classInfo = classInfoMapper.selectById(work.getClassId());
        if (classInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "班级不存在");
        }
        if (!canViewSubmission(principal, classInfo, stuWork)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "没有访问权限");
        }
        ClassGroup group = stuWork.getGroupId() == null ? null : classGroupMapper.selectById(stuWork.getGroupId());
        byte[] content = pdfExportService.renderSubmission(stuWork, work, classInfo, group);
        String base = (group == null ? "小组" + stuWork.getGroupId() : group.getGroupName())
                + "-" + work.getWorkName();
        return new ExportFile(content, safeFilename(base) + ".pdf", "application/pdf");
    }

    public ExportFile exportWorkPdfZip(Long workId, UserPrincipal principal) {
        TeacherWork work = teacherWorkMapper.selectById(workId);
        if (work == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "作业不存在");
        }
        ClassInfo classInfo = classInfoMapper.selectById(work.getClassId());
        if (classInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "班级不存在");
        }
        requireOwner(classInfo, principal);

        List<StuWork> submissions = stuWorkMapper.selectList(new LambdaQueryWrapper<StuWork>()
                .eq(StuWork::getTeacherWorkId, workId)
                .orderByAsc(StuWork::getGroupId));
        List<Long> groupIds = submissions.stream().map(StuWork::getGroupId).filter(Objects::nonNull)
                .distinct().toList();
        Map<Long, ClassGroup> groups = groupIds.isEmpty() ? Collections.emptyMap()
                : classGroupMapper.selectBatchIds(groupIds).stream()
                        .collect(Collectors.toMap(ClassGroup::getId, Function.identity()));

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ZipOutputStream zip = new ZipOutputStream(out, StandardCharsets.UTF_8)) {
            Set<String> usedNames = new HashSet<>();
            for (StuWork submission : submissions) {
                ClassGroup group = groups.get(submission.getGroupId());
                String base = group == null ? "group-" + submission.getGroupId() : group.getGroupName();
                String filename = uniqueEntryName(usedNames, safeFilename(base), submission.getId());
                byte[] pdf = pdfExportService.renderSubmission(submission, work, classInfo, group);
                zip.putNextEntry(new ZipEntry(filename));
                zip.write(pdf);
                zip.closeEntry();
            }
            zip.finish();
            String filename = safeFilename(work.getWorkName() + "-作业打包") + ".zip";
            return new ExportFile(out.toByteArray(), filename, "application/zip");
        } catch (IOException e) {
            log.error("Zip submissions failed, workId={}", workId, e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "打包导出失败");
        }
    }

    private boolean canViewSubmission(UserPrincipal principal, ClassInfo classInfo, StuWork stuWork) {
        if (principal == null) {
            return false;
        }
        if (principal.role() == Role.TEACHER) {
            return Objects.equals(classInfo.getTeacherId(), principal.userId());
        }
        if (principal.role() == Role.STUDENT && principal.stuId() != null) {
            StudentClass studentClass = studentClassMapper.selectOne(new LambdaQueryWrapper<StudentClass>()
                    .eq(StudentClass::getStuId, principal.stuId())
                    .eq(StudentClass::getClassId, classInfo.getId())
                    .last("limit 1"));
            return studentClass != null && Objects.equals(studentClass.getGroupId(), stuWork.getGroupId());
        }
        return false;
    }

    private ClassInfo requireOwnedClass(Long classId, UserPrincipal principal) {
        ClassInfo classInfo = classInfoMapper.selectById(classId);
        if (classInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "班级不存在");
        }
        requireOwner(classInfo, principal);
        return classInfo;
    }

    private void requireOwner(ClassInfo classInfo, UserPrincipal principal) {
        if (principal == null || principal.role() != Role.TEACHER
                || !Objects.equals(classInfo.getTeacherId(), principal.userId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只有班级负责教师可以导出");
        }
    }

    private String scoreKey(Long workId, Long groupId) {
        return workId + ":" + groupId;
    }

    private String uniqueEntryName(Set<String> used, String base, Long id) {
        String candidate = (base.isBlank() ? "group-" + id : base) + ".pdf";
        if (used.add(candidate)) {
            return candidate;
        }
        candidate = (base.isBlank() ? "group-" + id : base) + "-" + id + ".pdf";
        used.add(candidate);
        return candidate;
    }

    private String safeFilename(String value) {
        if (value == null || value.isBlank()) {
            return "export";
        }
        String name = value.replaceAll("[\\\\/:*?\"<>|\\r\\n\\t]", "_").trim();
        return name.length() > 80 ? name.substring(0, 80) : name;
    }
}
