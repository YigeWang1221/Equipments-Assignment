package com.zjgs.equip.greatwork.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("great_work")
public class GreatWork {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long stuWorkId;
    private String workName;
    private String photoUrl;
    private Integer classType;
    private Integer year;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
