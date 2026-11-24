package com.zjgs.equip.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("student")
public class Student {
    @TableId(type = IdType.INPUT)
    private Long stuId;
    private String stuName;
    private String phone;
    private String password;
    private String college;
    private String adminClassName;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
