package com.zjgs.equip.ws;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * 班级实时事件推送。前端订阅 /topic/classes/{classId} 获取作业、分组、借用等变更。
 */
@Component
public class RealtimePublisher {
    private final SimpMessagingTemplate messagingTemplate;

    public RealtimePublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void publishClassEvent(Long classId, String type, Object payload) {
        if (classId == null) {
            return;
        }
        RealtimeEvent event = new RealtimeEvent(type, payload, System.currentTimeMillis());
        messagingTemplate.convertAndSend("/topic/classes/" + classId, event);
    }

    public record RealtimeEvent(String type, Object payload, long timestamp) {
    }
}
