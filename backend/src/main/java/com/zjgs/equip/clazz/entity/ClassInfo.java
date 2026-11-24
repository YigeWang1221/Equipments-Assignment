package com.zjgs.equip.clazz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("class_info")
public class ClassInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String className;
    private Integer classType;
    private Integer year;
    private LocalDate startTime;
    private LocalDate endTime;
    private Long teacherId;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
