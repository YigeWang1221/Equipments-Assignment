package com.zjgs.equip.homework.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 作业内容视图。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkContentVO {
    private String contentHtml;
    private String contentJson;
    private Integer version;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
