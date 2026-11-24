package com.zjgs.equip.homework.scheduler;

import com.zjgs.equip.homework.entity.TeacherWork;
import com.zjgs.equip.homework.mapper.TeacherWorkMapper;
import com.zjgs.equip.homework.support.WorkStatusCalculator;
import com.zjgs.equip.ws.RealtimePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 定时刷新 teacher_work.status，保证数据库状态与起止时间一致。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WorkStatusScheduler {
    private final TeacherWorkMapper teacherWorkMapper;
    private final RealtimePublisher realtimePublisher;

    @Scheduled(fixedDelay = 60_000)
    public int refreshAll() {
        List<TeacherWork> works = teacherWorkMapper.selectList(null);
        int updated = 0;
        LocalDateTime now = LocalDateTime.now();
        for (TeacherWork work : works) {
            int computed = WorkStatusCalculator.calculate(work.getStartTime(), work.getEndTime(), now);
            if (Objects.equals(work.getStatus(), computed)) {
                continue;
            }
            TeacherWork patch = new TeacherWork();
            patch.setId(work.getId());
            patch.setStatus(computed);
            patch.setUpdatedAt(now);
            teacherWorkMapper.updateById(patch);
            updated++;
            realtimePublisher.publishClassEvent(work.getClassId(), "work.updated",
                    Map.of("workId", work.getId(), "classId", work.getClassId(), "status", computed));
        }
        if (updated > 0) {
            log.info("Refreshed {} teacher work status(es)", updated);
        }
        return updated;
    }
}
