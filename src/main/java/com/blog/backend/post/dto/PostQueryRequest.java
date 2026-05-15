package com.blog.backend.post.dto;

public record PostQueryRequest(
        Integer page,
        Integer pageSize,
        String keyword,
        Long categoryId,
        Long tagId,
        String status
) {
}
