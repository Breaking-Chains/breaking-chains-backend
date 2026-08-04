package com.breakingchains.security;

import com.breakingchains.exception.AppException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey key;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expiration-ms:900000}") long accessExpirationMs,
            @Value("${jwt.refresh-expiration-ms:604800000}") long refreshExpirationMs
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpirationMs = accessExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    public String generateAccessToken(String userId, String email, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessExpirationMs);

        return Jwts.builder()
                .subject(userId)
                .claim("email", email)
                .claim("role", role)
                .claim("type", "ACCESS")
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(String userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshExpirationMs);

        return Jwts.builder()
                .subject(userId)
                .claim("type", "REFRESH")
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public String getUserIdFromToken(String token) {
        return getClaims(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        return getClaims(token).get("role", String.class);
    }

    public Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException ex) {
            throw AppException.tokenExpired("Access token has expired");
        } catch (JwtException | IllegalArgumentException ex) {
            throw AppException.unauthorized("Invalid JWT token");
        }
    }

    public boolean validateAccessToken(String token) {
        try {
            Claims claims = getClaims(token);
            return "ACCESS".equals(claims.get("type"));
        } catch (AppException ex) {
            throw ex;
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = getClaims(token);
            return "REFRESH".equals(claims.get("type"));
        } catch (AppException ex) {
            throw AppException.invalidRefreshToken("Refresh token is invalid or expired");
        } catch (Exception ex) {
            throw AppException.invalidRefreshToken("Refresh token is invalid or expired");
        }
    }

    public long getRefreshExpirationMs() {
        return refreshExpirationMs;
    }
}
