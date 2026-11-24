package com.zjgs.equip.homework.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("teacher_work")
public class TeacherWork {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long classId;
    private String workName;
    private String contentHtml;
    private String contentJson;
    private String detailUrl;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
    private Long createdBy;
    private Integer version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
