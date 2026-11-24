package com.zjgs.equip.homework.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 教师评分请求。
 */
@Data
public class ScoreRequest {
    @NotNull(message = "分数不能为空")
    @DecimalMin(value = "0", message = "分数不能小于0")
    @DecimalMax(value = "100", message = "分数不能大于100")
    private BigDecimal score;
}
