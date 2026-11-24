package com.zjgs.equip.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
    private String secret = "zjgs-equip-management-jwt-secret-key-please-change-in-production-2024";
    private long expirationSeconds = 7200;
}
