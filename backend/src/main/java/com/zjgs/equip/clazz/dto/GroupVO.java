package com.zjgs.equip.clazz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 小组视图对象。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupVO {
    private Long id;
    private Long classId;
    private String className;
    private String groupName;
    private Integer groupNum;
    private String teachingClassName;
    /** 成员展示文本，例如 "2023000001 王亦格, 2023000002 陈一鸣" */
    private String members;
    private List<Long> memberStuIds;
    private List<String> memberNames;
}
