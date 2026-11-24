package com.zjgs.equip.equipment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增/编辑器材请求。
 */
@Data
public class EquipmentRequest {
    @NotBlank(message = "器材名称不能为空")
    @Size(max = 100, message = "器材名称不能超过100个字符")
    private String name;

    @Size(max = 500, message = "图片地址不能超过500个字符")
    private String photoUrl;

    @Size(max = 500, message = "资料地址不能超过500个字符")
    private String infoUrl;

    @NotNull(message = "器材总数不能为空")
    @Min(value = 0, message = "器材总数不能为负数")
    private Integer totalNumber;
}
