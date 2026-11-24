package com.zjgs.equip.auth;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConditionalOnProperty(name = "app.auth.store", havingValue = "redis")
public class RedisTokenStore implements TokenStore {
    private static final String PREFIX = "equip:token:";
    private final StringRedisTemplate redisTemplate;

    public RedisTokenStore(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void store(String jti, Long userId, long ttlSeconds) {
        redisTemplate.opsForValue().set(PREFIX + jti, String.valueOf(userId), Duration.ofSeconds(ttlSeconds));
    }

    @Override
    public boolean isActive(String jti) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + jti));
    }

    @Override
    public void revoke(String jti) {
        redisTemplate.delete(PREFIX + jti);
    }
}
