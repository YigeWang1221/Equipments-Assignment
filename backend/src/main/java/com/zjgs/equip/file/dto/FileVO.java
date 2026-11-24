package com.zjgs.equip.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileVO {
    private Long id;
    private String url;
    private String name;
    private Long size;
    private String contentType;
    private LocalDateTime createdAt;
}
