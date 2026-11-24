package com.zjgs.equip.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学生视图对象（含当前班级/小组信息）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentVO {
    private Long stuId;
    private String stuName;
    private String phone;
    private String college;
    private String adminClassName;
    private Long classId;
    private String className;
    private String teachingClassName;
    private Long groupId;
    private String groupName;
    private Integer status;
}
