package com.zjgs.equip.equipment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 器材展示对象。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentVO {
    private Long id;
    private String name;
    private String photoUrl;
    private String infoUrl;
    private Integer totalNumber;
    private Integer usedNumber;
    private Integer availableNumber;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
