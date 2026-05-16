package com.blog.backend.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        Bootstrap bootstrap,
        Redis redis,
        Jwt jwt,
        Cors cors
) {
    public record Bootstrap(boolean enabled, String adminUsername, String adminPassword) {}

    public record Redis(boolean enabled) {}

    public record Jwt(String issuer, String secret, long accessTokenExpiryMinutes, long refreshTokenExpiryDays) {}

    public record Cors(List<String> allowedOrigins) {}
}
