package com.blog.backend.post.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PostSummaryVO(
        Long id,
        String title,
        String slug,
        String summary,
        String status,
        boolean pinned,
        long viewCount,
        String authorName,
        CategoryVO category,
        List<TagVO> tags,
        LocalDateTime publishedAt,
        LocalDateTime createdAt
) {
}
