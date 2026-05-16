package com.blog.backend.store;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
@ConditionalOnProperty(prefix = "app.redis", name = "enabled", havingValue = "false")
public class InMemoryCommentRateLimiter implements CommentRateLimiter {
    private final Map<String, Instant> store = new ConcurrentHashMap<>();

    @Override
    public boolean allow(String key) {
        Instant now = Instant.now();
        Instant nextAllowed = store.get(key);
        if (nextAllowed != null && nextAllowed.isAfter(now)) {
            return false;
        }
        store.put(key, now.plusSeconds(30));
        return true;
    }
}
