/*
 * GUIDA DIDATTICA BACKEND
 * ---------------------------------------------------------------------------
 * SICUREZZA: gestisce password/JWT e funzioni necessarie all’autenticazione.
 * File: JwtUtil.java
 * Segui le annotazioni Spring (@RestController, @Entity, @Service...) per capire
 * quale ruolo assume la classe nel flusso richiesta -> logica -> database.
 */
package com.example.progettoalbergo.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId, String email, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key())
                .compact();
    }

    public Long extractUserId(String token) {
        Object userId = claim(token, "userId");
        return userId == null ? null : Long.valueOf(userId.toString());
    }

    public String extractRole(String token) {
        Object role = claim(token, "role");
        return role == null ? null : role.toString();
    }

    private Object claim(String token, String name) {
        return Jwts.parser().verifyWith(key()).build()
                .parseSignedClaims(token).getPayload().get(name);
    }

    public boolean isValid(String token) {
        try {
            Jwts.parser().verifyWith(key()).build().parseSignedClaims(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}
