package com.zjgs.equip.homework.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("stu_work")
public class StuWork {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long teacherWorkId;
    private Long groupId;
    private String workDoc;
    private String videoUrl;
    private String zipUrl;
    private BigDecimal score;
    private Integer submitStatus;
    private LocalDateTime submittedAt;
    private Integer version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
