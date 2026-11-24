package com.zjgs.equip.greatwork.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 优秀作业列表视图。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GreatWorkVO {
    private Long id;
    private Long stuWorkId;
    private String workName;
    private String photoUrl;
    private Integer classType;
    private Integer year;
    private String members;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
