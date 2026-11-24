package com.zjgs.equip.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentStatsVO {
    private long totalEquipmentTypes;
    private long totalStock;
    private long usedStock;
    private long availableStock;
    private long applyingBorrowCount;
    private long borrowedCount;
}
