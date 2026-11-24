package com.zjgs.equip.greatwork.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 编辑优秀作品请求。
 */
@Data
public class GreatWorkUpdateRequest {
    @NotBlank(message = "作品名称不能为空")
    private String workName;

    private String photoUrl;

    private Integer classType;
}
