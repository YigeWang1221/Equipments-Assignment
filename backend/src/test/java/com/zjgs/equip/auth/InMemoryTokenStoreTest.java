package com.zjgs.equip.auth;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryTokenStoreTest {
    @Test
    void storesAndRevokesTokens() {
        InMemoryTokenStore store = new InMemoryTokenStore();
        store.store("jti-1", 1L, 60);

        assertThat(store.isActive("jti-1")).isTrue();

        store.revoke("jti-1");
        assertThat(store.isActive("jti-1")).isFalse();
    }

    @Test
    void unknownTokenIsInactive() {
        InMemoryTokenStore store = new InMemoryTokenStore();
        assertThat(store.isActive("missing")).isFalse();
    }
}
