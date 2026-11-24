package com.zjgs.equip.homework.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 教师作业详情（含富文本内容）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WorkDetailVO extends WorkVO {
    private String contentHtml;
    private String contentJson;
}
