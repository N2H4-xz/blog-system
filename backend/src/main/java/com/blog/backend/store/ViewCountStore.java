package com.blog.backend.store;

import java.util.Map;

public interface ViewCountStore {
    void increment(Long postId);

    long getPendingIncrement(Long postId);

    Map<Long, Long> drainAll();
}
