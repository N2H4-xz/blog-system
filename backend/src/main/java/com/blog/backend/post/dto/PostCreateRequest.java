package com.blog.backend.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record PostCreateRequest(
        @NotBlank @Size(max = 160) String title,
        @Size(max = 200) String summary,
        @NotBlank String contentMarkdown,
        Long categoryId,
        @NotEmpty Set<Long> tagIds,
        String status
) {
}
