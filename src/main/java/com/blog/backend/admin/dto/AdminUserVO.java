package com.blog.backend.admin.dto;

public record AdminUserVO(Long id, String username, String displayName, String role, boolean enabled) {
}
