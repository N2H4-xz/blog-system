package com.blog.backend.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentCreateRequest(
        @NotBlank @Size(max = 400) String content,
        @Size(max = 60) String guestName,
        @Size(max = 120) String guestEmail,
        @Size(max = 200) String guestWebsite
) {
}
