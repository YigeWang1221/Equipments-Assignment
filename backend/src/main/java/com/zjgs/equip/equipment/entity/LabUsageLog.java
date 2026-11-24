package com.zjgs.equip.equipment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("lab_usage_log")
public class LabUsageLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long stuId;
    private Long classId;
    private Long borrowId;
    private String eventType;
    private LocalDateTime occurredAt;
}
