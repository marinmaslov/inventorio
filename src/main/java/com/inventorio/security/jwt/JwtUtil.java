package com.inventorio.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger logger = LogManager.getLogger(JwtUtil.class);
    private final String secretKey;

    public JwtUtil(@Value("${jwt.secret}") String secretKey) {
        if (secretKey == null || secretKey.getBytes().length < 32) {
            throw new IllegalArgumentException("JWT secret key must be at least 32 bytes (256 bits) for HS256.");
        }
        this.secretKey = secretKey;
    }

    public String generateToken(String username) {
        try {
            return Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                    .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            logger.error("Failed to generate token", e);
            throw new RuntimeException("Failed to generate token: " + e.getMessage(), e);
        }
    }

    public String extractUsername(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (Exception e) {
            logger.error("Failed to extract username", e);
            throw new RuntimeException("Failed to extract username: " + e.getMessage(), e);
        }
    }

    public boolean validateToken(String token, String username) {
        try {
            return extractUsername(token).equals(username) && !isTokenExpired(token);
        } catch (Exception e) {
            logger.error("Failed to validate token", e);
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            logger.error("Failed to check token expiration", e);
            throw new RuntimeException("Failed to check token expiration: " + e.getMessage(), e);
        }
    }
}