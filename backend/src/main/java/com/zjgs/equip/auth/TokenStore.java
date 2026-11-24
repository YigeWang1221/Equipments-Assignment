package com.zjgs.equip.auth;

public interface TokenStore {
    void store(String jti, Long userId, long ttlSeconds);

    boolean isActive(String jti);

    void revoke(String jti);
}
