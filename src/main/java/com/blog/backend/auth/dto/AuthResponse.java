package com.blog.backend.auth.dto;

public record AuthResponse(String accessToken, String refreshToken, CurrentUserVO user) {
}
