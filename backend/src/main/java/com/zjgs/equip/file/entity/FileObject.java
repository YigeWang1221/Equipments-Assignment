package com.zjgs.equip.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("file_object")
public class FileObject {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String storageType;
    private String storageKey;
    private String originalName;
    private String contentType;
    private Long sizeBytes;
    private String url;
    private Long uploaderId;
    private LocalDateTime createdAt;
}
