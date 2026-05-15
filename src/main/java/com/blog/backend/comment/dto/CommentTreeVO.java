package com.blog.backend.comment.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record CommentTreeVO(
        Long id,
        String authorName,
        String content,
        String status,
        boolean guest,
        boolean canDelete,
        LocalDateTime createdAt,
        List<CommentTreeVO> replies
) {
    public static CommentTreeVO of(
            Long id,
            String authorName,
            String content,
            String status,
            boolean guest,
            boolean canDelete,
            LocalDateTime createdAt
    ) {
        return new CommentTreeVO(id, authorName, content, status, guest, canDelete, createdAt, new ArrayList<>());
    }
}
