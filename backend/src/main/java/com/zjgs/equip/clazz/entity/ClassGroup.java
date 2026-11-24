package com.zjgs.equip.clazz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("class_group")
public class ClassGroup {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long classId;
    private String groupName;
    private Integer groupNum;
    private String teachingClassName;
    private String membersText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
