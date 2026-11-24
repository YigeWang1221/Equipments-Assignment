package com.zjgs.equip.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceStatVO {
    private Long stuId;
    private String stuName;
    private String className;
    private long borrowCount;
    private LocalDateTime lastBorrowAt;
}
