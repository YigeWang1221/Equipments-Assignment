package com.zjgs.equip.homework.controller;

import com.zjgs.equip.auth.CurrentUser;
import com.zjgs.equip.common.ApiResponse;
import com.zjgs.equip.greatwork.dto.GreatWorkCreateRequest;
import com.zjgs.equip.greatwork.dto.GreatWorkVO;
import com.zjgs.equip.greatwork.service.GreatWorkService;
import com.zjgs.equip.homework.dto.ScoreRequest;
import com.zjgs.equip.homework.dto.SubmissionDetailVO;
import com.zjgs.equip.homework.dto.SubmissionUpdateRequest;
import com.zjgs.equip.homework.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/submissions")
@RequiredArgsConstructor
public class SubmissionController {
    private final SubmissionService submissionService;
    private final GreatWorkService greatWorkService;
    private final CurrentUser currentUser;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','STUDENT')")
    public ApiResponse<SubmissionDetailVO> get(@PathVariable("id") Long id) {
        return ApiResponse.ok(submissionService.get(id, currentUser.require()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<SubmissionDetailVO> update(@PathVariable("id") Long id,
                                                   @RequestBody SubmissionUpdateRequest request) {
        return ApiResponse.ok(submissionService.update(id, request, currentUser.studentId()));
    }

    @PostMapping("/{id}/score")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<SubmissionDetailVO> score(@PathVariable("id") Long id,
                                                  @Valid @RequestBody ScoreRequest request) {
        return ApiResponse.ok(submissionService.score(id, request, currentUser.teacherId()));
    }

    @PostMapping("/{id}/great-work")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<GreatWorkVO> createGreatWork(@PathVariable("id") Long id,
                                                    @Valid @RequestBody GreatWorkCreateRequest request) {
        return ApiResponse.ok(greatWorkService.create(id, request, currentUser.teacherId()));
    }
}
