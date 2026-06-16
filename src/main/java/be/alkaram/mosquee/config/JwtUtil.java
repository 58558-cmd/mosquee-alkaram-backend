package be.alkaram.mosquee.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Base64;

@Component
public class JwtUtil {

    private static final long EXPIRATION = 8 * 60 * 60 * 1000; // 8 heures

    @Value("${jwt.secret:alkaram-mosquee-jwt-secret-key-must-be-at-least-32-chars-long!}")
    private String secret;

    private SecretKey getKey() {
        byte[] keyBytes = Base64.getEncoder().encode(secret.getBytes());
        // S'assurer que la clé est assez longue pour HS256
        byte[] paddedKey = new byte[32];
        System.arraycopy(keyBytes, 0, paddedKey, 0, Math.min(keyBytes.length, 32));
        return Keys.hmacShaKeyFor(paddedKey);
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getKey())
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser().verifyWith(getKey()).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean isValid(String token) {
        try {
            Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}