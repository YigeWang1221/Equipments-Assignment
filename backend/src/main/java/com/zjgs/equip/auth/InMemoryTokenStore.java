package com.zjgs.equip.auth;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ConditionalOnProperty(name = "app.auth.store", havingValue = "memory", matchIfMissing = true)
public class InMemoryTokenStore implements TokenStore {
    private final Map<String, Entry> tokens = new ConcurrentHashMap<>();

    @Override
    public void store(String jti, Long userId, long ttlSeconds) {
        cleanup();
        tokens.put(jti, new Entry(userId, Instant.now().plusSeconds(ttlSeconds)));
    }

    @Override
    public boolean isActive(String jti) {
        Entry entry = tokens.get(jti);
        if (entry == null) {
            return false;
        }
        if (entry.expiresAt.isBefore(Instant.now())) {
            tokens.remove(jti);
            return false;
        }
        return true;
    }

    @Override
    public void revoke(String jti) {
        tokens.remove(jti);
    }

    private void cleanup() {
        Instant now = Instant.now();
        tokens.entrySet().removeIf(entry -> entry.getValue().expiresAt.isBefore(now));
    }

    private record Entry(Long userId, Instant expiresAt) {
    }
}
