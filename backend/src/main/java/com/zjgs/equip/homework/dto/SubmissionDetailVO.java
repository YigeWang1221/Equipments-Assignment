package com.zjgs.equip.homework.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 小组作业提交详情。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionDetailVO {
    private Long id;
    private Long teacherWorkId;
    /** 与 teacherWorkId 相同，便于前端统一使用 workId。 */
    private Long workId;
    private String workName;
    private Long classId;
    private String className;
    private Long groupId;
    private String groupName;
    private Integer groupNum;
    private String members;
    private String workDoc;
    private String videoUrl;
    private String zipUrl;
    private BigDecimal score;
    private Integer submitStatus;
    private String submitStatusName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submittedAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
