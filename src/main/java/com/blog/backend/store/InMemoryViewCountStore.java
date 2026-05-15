package com.blog.backend.store;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
@ConditionalOnProperty(prefix = "app.redis", name = "enabled", havingValue = "false")
public class InMemoryViewCountStore implements ViewCountStore {
    private final Map<Long, Long> counts = new ConcurrentHashMap<>();

    @Override
    public void increment(Long postId) {
        counts.merge(postId, 1L, Long::sum);
    }

    @Override
    public long getPendingIncrement(Long postId) {
        return counts.getOrDefault(postId, 0L);
    }

    @Override
    public Map<Long, Long> drainAll() {
        Map<Long, Long> snapshot = new HashMap<>(counts);
        counts.clear();
        return snapshot;
    }
}
