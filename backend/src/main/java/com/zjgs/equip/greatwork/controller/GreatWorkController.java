package com.zjgs.equip.greatwork.controller;

import com.zjgs.equip.auth.CurrentUser;
import com.zjgs.equip.common.ApiResponse;
import com.zjgs.equip.greatwork.dto.GreatWorkDetailVO;
import com.zjgs.equip.greatwork.dto.GreatWorkUpdateRequest;
import com.zjgs.equip.greatwork.dto.GreatWorkVO;
import com.zjgs.equip.greatwork.service.GreatWorkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 优秀作业展示。GET 接口对访客公开（见 SecurityConfig）。
 */
@RestController
@RequestMapping("/api/v1/great-works")
@RequiredArgsConstructor
public class GreatWorkController {
    private final GreatWorkService greatWorkService;
    private final CurrentUser currentUser;

    @GetMapping
    public ApiResponse<List<GreatWorkVO>> list(@RequestParam(value = "year", required = false) Integer year,
                                                @RequestParam(value = "classType", required = false) Integer classType) {
        return ApiResponse.ok(greatWorkService.list(year, classType));
    }

    @GetMapping("/{id}")
    public ApiResponse<GreatWorkDetailVO> get(@PathVariable("id") Long id) {
        return ApiResponse.ok(greatWorkService.get(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<GreatWorkVO> update(@PathVariable("id") Long id,
                                            @Valid @RequestBody GreatWorkUpdateRequest request) {
        return ApiResponse.ok(greatWorkService.update(id, request, currentUser.teacherId()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        greatWorkService.delete(id, currentUser.teacherId());
        return ApiResponse.ok();
    }
}
