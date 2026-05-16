package com.blog.backend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(min = 4, max = 32) String username,
        @NotBlank @Size(min = 6, max = 64) String password,
        @NotBlank @Size(max = 60) String displayName
) {
}
