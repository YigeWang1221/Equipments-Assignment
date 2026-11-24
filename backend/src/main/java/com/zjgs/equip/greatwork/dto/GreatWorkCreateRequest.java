package com.zjgs.equip.greatwork.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 从已批改作业创建优秀作品请求。
 */
@Data
public class GreatWorkCreateRequest {
    @NotBlank(message = "作品名称不能为空")
    private String workName;

    private String photoUrl;

    /** 为空时取作业所在班级类型。 */
    private Integer classType;

    /** 为空时取作业所在班级年份。 */
    private Integer year;
}
