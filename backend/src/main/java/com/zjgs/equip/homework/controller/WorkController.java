package com.zjgs.equip.homework.controller;

import com.zjgs.equip.auth.CurrentUser;
import com.zjgs.equip.common.ApiResponse;
import com.zjgs.equip.homework.dto.SubmissionDetailVO;
import com.zjgs.equip.homework.dto.SubmissionSummaryVO;
import com.zjgs.equip.homework.dto.WorkContentVO;
import com.zjgs.equip.homework.dto.WorkDetailVO;
import com.zjgs.equip.homework.dto.WorkRequest;
import com.zjgs.equip.homework.dto.WorkVO;
import com.zjgs.equip.homework.service.SubmissionService;
import com.zjgs.equip.homework.service.WorkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/works")
@RequiredArgsConstructor
public class WorkController {
    private final WorkService workService;
    private final SubmissionService submissionService;
    private final CurrentUser currentUser;

    @GetMapping
    public ApiResponse<List<WorkVO>> list(@RequestParam(value = "classId", required = false) Long classId,
                                          @RequestParam(value = "status", required = false) Integer status) {
        return ApiResponse.ok(workService.list(classId, status));
    }

    @GetMapping("/{id}")
    public ApiResponse<WorkDetailVO> get(@PathVariable("id") Long id) {
        return ApiResponse.ok(workService.get(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<WorkDetailVO> create(@Valid @RequestBody WorkRequest request) {
        return ApiResponse.ok(workService.create(request, currentUser.teacherId()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<WorkDetailVO> update(@PathVariable("id") Long id,
                                             @Valid @RequestBody WorkRequest request) {
        return ApiResponse.ok(workService.update(id, request, currentUser.teacherId()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        workService.delete(id, currentUser.teacherId());
        return ApiResponse.ok();
    }

    @GetMapping("/{id}/content")
    public ApiResponse<WorkContentVO> content(@PathVariable("id") Long id) {
        return ApiResponse.ok(workService.content(id));
    }

    @GetMapping("/{id}/submissions")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<List<SubmissionSummaryVO>> submissions(@PathVariable("id") Long id) {
        return ApiResponse.ok(submissionService.listByWork(id, currentUser.teacherId()));
    }

    @PostMapping("/{id}/submissions/init")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<SubmissionDetailVO> initSubmission(@PathVariable("id") Long id) {
        return ApiResponse.ok(submissionService.init(id, currentUser.studentId()));
    }

    @GetMapping("/{id}/my-submission")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<SubmissionDetailVO> mySubmission(@PathVariable("id") Long id) {
        return ApiResponse.ok(submissionService.mySubmission(id, currentUser.studentId()));
    }
}
