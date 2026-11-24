package com.zjgs.equip.ws;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.util.Map;

/**
 * 简单的 WebSocket 心跳/连通性测试。
 */
@Controller
public class PingController {

    @MessageMapping("/ping")
    @SendTo("/topic/ping")
    public Map<String, Object> ping(Map<String, Object> body) {
        return Map.of("type", "PONG", "payload", body == null ? Map.of() : body, "timestamp", Instant.now().toEpochMilli());
    }
}
