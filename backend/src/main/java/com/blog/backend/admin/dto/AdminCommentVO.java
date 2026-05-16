package com.blog.backend.admin.dto;

import java.time.LocalDateTime;

public record AdminCommentVO(
        Long id,
        String postTitle,
        String authorName,
        String content,
        String status,
        boolean guest,
        LocalDateTime createdAt
) {
}
