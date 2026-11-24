package com.zjgs.equip.homework.support;

import com.zjgs.equip.common.BusinessException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 兼容前端常见日期时间格式的解析器。
 */
public final class FlexibleDateTime {
    private static final DateTimeFormatter[] DATE_TIME_FORMATTERS = new DateTimeFormatter[]{
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")
    };

    private FlexibleDateTime() {
    }

    public static LocalDateTime parse(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String text = value.trim();
        try {
            return LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException ignored) {
            // try the remaining patterns
        }
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(text, formatter);
            } catch (DateTimeParseException ignored) {
                // continue
            }
        }
        try {
            return LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        } catch (DateTimeParseException ignored) {
            // continue
        }
        try {
            return LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyy/MM/dd")).atStartOfDay();
        } catch (DateTimeParseException ignored) {
            throw new BusinessException("时间格式不正确: " + value);
        }
    }

    /** 仅日期格式时按当天 23:59:59 处理，适合作业截止时间。 */
    public static LocalDateTime parseEnd(String value) {
        LocalDateTime parsed = parse(value);
        if (parsed == null) {
            return null;
        }
        String text = value.trim();
        if (text.matches("\\d{4}-\\d{2}-\\d{2}") || text.matches("\\d{4}/\\d{2}/\\d{2}")) {
            return parsed.toLocalDate().atTime(23, 59, 59);
        }
        return parsed;
    }
}
