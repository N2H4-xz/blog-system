package com.blog.backend.security;

import com.blog.backend.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final AppProperties appProperties;
    private final SecretKey secretKey;

    public JwtService(AppProperties appProperties) {
        this.appProperties = appProperties;
        byte[] bytes = appProperties.jwt().secret().length() >= 32
                ? appProperties.jwt().secret().getBytes(StandardCharsets.UTF_8)
                : Decoders.BASE64.decode(appProperties.jwt().secret());
        this.secretKey = Keys.hmacShaKeyFor(bytes);
    }

    public String generateAccessToken(AppUserPrincipal principal) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(appProperties.jwt().issuer())
                .subject(principal.getUsername())
                .claims(Map.of("uid", principal.getId(), "role", principal.getRole()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(appProperties.jwt().accessTokenExpiryMinutes(), ChronoUnit.MINUTES)))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(AppUserPrincipal principal) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(appProperties.jwt().issuer())
                .subject(principal.getUsername())
                .claims(Map.of("uid", principal.getId(), "type", "refresh"))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(appProperties.jwt().refreshTokenExpiryDays(), ChronoUnit.DAYS)))
                .signWith(secretKey)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return parse(token).getSubject();
    }

    public Long extractUserId(String token) {
        Number number = parse(token).get("uid", Number.class);
        return number.longValue();
    }
}
