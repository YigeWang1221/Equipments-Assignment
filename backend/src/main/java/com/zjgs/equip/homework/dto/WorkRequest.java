package com.zjgs.equip.homework.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 新建/编辑作业请求。时间字段接受 "yyyy-MM-dd HH:mm:ss"、ISO 或 "yyyy-MM-dd"。
 */
@Data
public class WorkRequest {
    @NotNull(message = "班级不能为空")
    private Long classId;

    @NotBlank(message = "作业名称不能为空")
    private String workName;

    private String contentHtml;

    private String contentJson;

    private String detailUrl;

    private String startTime;

    private String endTime;
}
