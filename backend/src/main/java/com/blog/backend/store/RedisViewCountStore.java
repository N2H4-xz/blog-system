package com.blog.backend.store;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.redis", name = "enabled", havingValue = "true")
public class RedisViewCountStore implements ViewCountStore {
    private final StringRedisTemplate redisTemplate;
    private static final String PREFIX = "blog:view:";

    @Override
    public void increment(Long postId) {
        redisTemplate.opsForValue().increment(PREFIX + postId);
    }

    @Override
    public long getPendingIncrement(Long postId) {
        String value = redisTemplate.opsForValue().get(PREFIX + postId);
        return value == null ? 0 : Long.parseLong(value);
    }

    @Override
    public Map<Long, Long> drainAll() {
        Set<String> keys = redisTemplate.keys(PREFIX + "*");
        Map<Long, Long> result = new HashMap<>();
        if (keys == null || keys.isEmpty()) {
            return result;
        }
        for (String key : keys) {
            String value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                result.put(Long.parseLong(key.substring(PREFIX.length())), Long.parseLong(value));
            }
        }
        redisTemplate.delete(keys);
        return result;
    }
}
