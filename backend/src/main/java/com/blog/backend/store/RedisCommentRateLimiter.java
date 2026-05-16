package com.blog.backend.store;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.redis", name = "enabled", havingValue = "true")
public class RedisCommentRateLimiter implements CommentRateLimiter {
    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean allow(String key) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent("blog:comment-limit:" + key, "1", Duration.ofSeconds(30));
        return Boolean.TRUE.equals(success);
    }
}
