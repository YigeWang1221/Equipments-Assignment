package com.zjgs.equip.clazz.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 教学班级视图对象。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassVO {
    private Long id;
    private String className;
    /** 1=ARM嵌入式 2=IOT物联网 */
    private Integer classType;
    private String classTypeName;
    private Integer year;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startTime;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endTime;
    private Long teacherId;
    private String teacherName;
    private Integer studentCount;
    private Integer groupCount;
    private Integer workCount;
    private Integer status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
