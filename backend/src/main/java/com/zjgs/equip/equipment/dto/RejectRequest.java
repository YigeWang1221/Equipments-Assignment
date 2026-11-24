package com.zjgs.equip.equipment.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 教师拒绝借用申请，可填写备注。
 */
@Data
public class RejectRequest {
    @Size(max = 500, message = "备注不能超过500个字符")
    private String remark;
}
