package com.zjgs.equip.homework.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 小组提交摘要。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionSummaryVO {
    private Long id;
    private Long teacherWorkId;
    private Long groupId;
    private String groupName;
    private Integer groupNum;
    private String members;
    private BigDecimal score;
    private Integer submitStatus;
    private String submitStatusName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submittedAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
