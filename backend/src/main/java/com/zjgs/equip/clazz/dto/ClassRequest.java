package com.zjgs.equip.clazz.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 新建/编辑教学班级请求。
 */
@Data
public class ClassRequest {
    @NotBlank(message = "班级名称不能为空")
    private String className;

    @NotNull(message = "班级类型不能为空")
    private Integer classType;

    @NotNull(message = "年份不能为空")
    private Integer year;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endTime;
}
