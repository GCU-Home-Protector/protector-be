package com.gachon.home_protector.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JWTUtil {

    private final SecretKey key;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        byte[] byteSecretKey = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(byteSecretKey);
    }

    public String getUsername(String token) {
        return extractPayload(token)
                .get("username", String.class);
    }

    public String getRole(String token) {
        return extractPayload(token)
                .get("role", String.class);
    }

    public String getTokenType(String token) {
        return extractPayload(token)
                .get("tokenType", String.class);
    }

    public Boolean isExpired(String token, Date currentTime) {
        Date expiration = extractPayload(token)
                .getExpiration();

        return expiration.before(currentTime);
    }

    public String createJwt(String tokenType, String username, String role, Long expiredMs) {
        return Jwts.builder()
                .claim("tokenType", tokenType)
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(key)
                .compact();
    }

    private Claims extractPayload(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
