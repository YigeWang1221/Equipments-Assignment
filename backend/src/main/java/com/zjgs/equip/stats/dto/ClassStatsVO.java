package com.zjgs.equip.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassStatsVO {
    private Long classId;
    private String className;
    private long studentCount;
    private long groupCount;
    private long workCount;
    private long submissionCount;
    private long gradedCount;
    private BigDecimal averageScore;
    private long attendanceCount;
}
