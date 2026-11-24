package com.zjgs.equip.stats;

import com.zjgs.equip.common.ApiResponse;
import com.zjgs.equip.stats.dto.AttendanceStatVO;
import com.zjgs.equip.stats.dto.ClassStatsVO;
import com.zjgs.equip.stats.dto.EquipmentStatsVO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/stats")
public class StatsController {
    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/classes/{classId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<ClassStatsVO> classStats(@PathVariable Long classId) {
        return ApiResponse.ok(statsService.classStats(classId));
    }

    @GetMapping("/equipments")
    public ApiResponse<EquipmentStatsVO> equipmentStats() {
        return ApiResponse.ok(statsService.equipmentStats());
    }

    @GetMapping("/attendance")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<List<AttendanceStatVO>> attendance(
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ApiResponse.ok(statsService.attendance(classId, from, to));
    }
}
