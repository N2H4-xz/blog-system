package com.blog.backend.post.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PostDetailVO(
        Long id,
        String title,
        String slug,
        String summary,
        String contentMarkdown,
        String contentHtml,
        String status,
        boolean pinned,
        long viewCount,
        String authorName,
        Long authorId,
        CategoryVO category,
        List<TagVO> tags,
        LocalDateTime publishedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
