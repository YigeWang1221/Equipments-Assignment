package com.zjgs.equip.equipment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("equipment_borrow")
public class EquipmentBorrow {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long equipmentId;
    private Long stuId;
    private String stuName;
    private Integer num;
    private Integer status;
    private LocalDateTime applyTime;
    private LocalDateTime approveTime;
    private LocalDateTime returnTime;
    private Long operatorId;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
