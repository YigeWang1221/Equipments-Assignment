package com.zjgs.equip.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 新建/编辑学生请求。
 */
@Data
public class StudentRequest {
    /** 新建时必填；编辑时若传值必须与路径一致。 */
    private Long stuId;

    @NotBlank(message = "学生姓名不能为空")
    private String stuName;

    private String phone;

    /** 为空时新建默认 123456，编辑时忽略。 */
    private String password;

    private String college;

    private String adminClassName;

    @NotNull(message = "班级不能为空")
    private Long classId;

    private String teachingClassName;

    /** 选填，分配小组。 */
    private Long groupId;
}
