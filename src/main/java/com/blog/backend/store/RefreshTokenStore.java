package com.blog.backend.store;

public interface RefreshTokenStore {
    void save(String refreshToken, Long userId, long ttlSeconds);

    boolean contains(String refreshToken);

    void delete(String refreshToken);
}
