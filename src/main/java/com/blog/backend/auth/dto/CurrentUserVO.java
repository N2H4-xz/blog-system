package com.blog.backend.auth.dto;

public record CurrentUserVO(Long id, String username, String displayName, String role) {
}
