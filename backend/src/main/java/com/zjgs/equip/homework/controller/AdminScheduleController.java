package com.zjgs.equip.homework.controller;

import com.zjgs.equip.common.ApiResponse;
import com.zjgs.equip.homework.scheduler.WorkStatusScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/schedule")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TEACHER')")
public class AdminScheduleController {
    private final WorkStatusScheduler workStatusScheduler;

    /** 手动触发一次作业状态刷新。 */
    @PostMapping("/daily")
    public ApiResponse<Map<String, Object>> triggerDaily() {
        int updated = workStatusScheduler.refreshAll();
        return ApiResponse.ok(Map.of("updated", updated));
    }
}
