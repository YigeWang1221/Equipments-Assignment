package com.zjgs.equip.equipment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 学生提交借用申请。
 */
@Data
public class BorrowRequest {
    @NotNull(message = "借用数量不能为空")
    @Min(value = 1, message = "借用数量必须大于0")
    private Integer num;
}
