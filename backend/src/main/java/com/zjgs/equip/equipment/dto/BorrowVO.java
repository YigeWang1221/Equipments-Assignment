package com.zjgs.equip.equipment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 借用记录展示对象。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowVO {
    private Long id;
    private Long equipmentId;
    private String equipmentName;
    private Long stuId;
    private String stuName;
    private Integer num;
    private Integer status;
    private String statusName;
    private LocalDateTime applyTime;
    private LocalDateTime approveTime;
    private LocalDateTime returnTime;
    private Integer availableNumber;
    private Long operatorId;
    private String remark;
}
