package com.zjgs.equip.clazz.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 新建/编辑小组请求。编辑时 classId 可为空（不允许修改所属班级）。
 */
@Data
public class GroupRequest {
    private Long classId;

    @NotBlank(message = "小组名称不能为空")
    private String groupName;

    private String teachingClassName;

    /** 为空表示不修改成员；空数组表示清空成员。 */
    private List<Long> memberStuIds;
}
