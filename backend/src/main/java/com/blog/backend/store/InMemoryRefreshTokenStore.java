package com.blog.backend.store;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class InMemoryRefreshTokenStore implements RefreshTokenStore {
    private final Map<String, Long> store = new ConcurrentHashMap<>();
    private final Map<String, Instant> expirations = new ConcurrentHashMap<>();

    @Override
    public void save(String refreshToken, Long userId, long ttlSeconds) {
        store.put(refreshToken, userId);
        expirations.put(refreshToken, Instant.now().plusSeconds(ttlSeconds));
    }

    @Override
    public boolean contains(String refreshToken) {
        Instant expiry = expirations.get(refreshToken);
        if (expiry == null || expiry.isBefore(Instant.now())) {
            store.remove(refreshToken);
            expirations.remove(refreshToken);
            return false;
        }
        return store.containsKey(refreshToken);
    }

    @Override
    public void delete(String refreshToken) {
        store.remove(refreshToken);
        expirations.remove(refreshToken);
    }
}
