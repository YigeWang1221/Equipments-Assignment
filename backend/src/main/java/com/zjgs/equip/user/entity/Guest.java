package com.zjgs.equip.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("guest")
public class Guest {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String phone;
    private String name;
    private LocalDateTime createdAt;
}
