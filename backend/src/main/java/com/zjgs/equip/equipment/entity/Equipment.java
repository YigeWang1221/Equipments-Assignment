package com.zjgs.equip.equipment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("equipment")
public class Equipment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String photoUrl;
    private String infoUrl;
    private Integer totalNumber;
    private Integer usedNumber;
    private Integer status;
    private Integer version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
