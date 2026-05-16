package com.blog.backend.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentReplyRequest(@NotBlank @Size(max = 400) String content) {
}
