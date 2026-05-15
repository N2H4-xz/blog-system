package com.blog.backend.store;

public interface CommentRateLimiter {
    boolean allow(String key);
}
