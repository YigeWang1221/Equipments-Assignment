package com.zjgs.equip.homework.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 教师作业列表视图。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkVO {
    private Long id;
    private Long classId;
    private String className;
    private String workName;
    /** 0未开始 1进行中 2截止 */
    private Integer status;
    private String statusName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    private String detailUrl;
    private Integer groupTotal;
    private Integer groupSubmitted;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
