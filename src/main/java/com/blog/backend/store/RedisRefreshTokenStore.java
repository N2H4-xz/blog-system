package com.blog.backend.store;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.redis", name = "enabled", havingValue = "true")
public class RedisRefreshTokenStore implements RefreshTokenStore {
    private final StringRedisTemplate redisTemplate;

    @Override
    public void save(String refreshToken, Long userId, long ttlSeconds) {
        redisTemplate.opsForValue().set("blog:refresh:" + refreshToken, String.valueOf(userId), Duration.ofSeconds(ttlSeconds));
    }

    @Override
    public boolean contains(String refreshToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("blog:refresh:" + refreshToken));
    }

    @Override
    public void delete(String refreshToken) {
        redisTemplate.delete("blog:refresh:" + refreshToken);
    }
}
