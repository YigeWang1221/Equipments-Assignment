package com.zjgs.equip.homework.dto;

import lombok.Data;

/**
 * 学生保存/提交作业请求。
 */
@Data
public class SubmissionUpdateRequest {
    private String workDoc;
    private String videoUrl;
    private String zipUrl;
    /** false=保存草稿，true=提交。 */
    private Boolean submit;
}
