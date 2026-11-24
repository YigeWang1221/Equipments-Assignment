package com.zjgs.equip.homework.support;

import com.zjgs.equip.common.WorkStatus;

import java.time.LocalDateTime;

/**
 * 根据起止时间实时计算作业状态：0未开始 1进行中 2截止。
 */
public final class WorkStatusCalculator {
    private WorkStatusCalculator() {
    }

    public static int calculate(LocalDateTime startTime, LocalDateTime endTime) {
        return calculate(startTime, endTime, LocalDateTime.now());
    }

    public static int calculate(LocalDateTime startTime, LocalDateTime endTime, LocalDateTime now) {
        LocalDateTime current = now == null ? LocalDateTime.now() : now;
        if (startTime != null && current.isBefore(startTime)) {
            return WorkStatus.NOT_STARTED.getCode();
        }
        if (endTime != null && current.isAfter(endTime)) {
            return WorkStatus.ENDED.getCode();
        }
        return WorkStatus.ONGOING.getCode();
    }

    public static String statusName(Integer status) {
        return WorkStatus.fromCode(status == null ? 0 : status).getLabel();
    }
}
