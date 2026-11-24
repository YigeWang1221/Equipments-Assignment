package com.zjgs.equip.greatwork.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优秀作业详情（含提交内容）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GreatWorkDetailVO extends GreatWorkVO {
    private String workDoc;
    private String videoUrl;
    private String zipUrl;
    private BigDecimal score;
    private String className;
    private String groupName;
    private Integer groupNum;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submittedAt;
}
