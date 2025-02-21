package com.gachon.home_protector.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.crypto.SecretKey;
import java.util.Calendar;
import java.util.Date;

@Component
public class JWTUtil {

    private final SecretKey key;
    private static final int ACCESS_TOKEN_EXPIRED_HOUR = 1; // 1시간
    private static final int REFRESH_TOKEN_EXPIRED_HOUR = 24; // 하루

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

    public String createAccessToken(String username, String role, Date currentTime) {

        validateUserInfoAndCurrentTime(username, role, currentTime);

        return Jwts.builder()
                .claim("tokenType", "access")
                .claim("username", username)
                .claim("role", role)
                .issuedAt(currentTime)
                .expiration(calculateExpirationOfTokenBasedonCurrentTime(currentTime, ACCESS_TOKEN_EXPIRED_HOUR))
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(String username, String role, Date currentTime) {

        validateUserInfoAndCurrentTime(username, role, currentTime);

        return Jwts.builder()
                .claim("tokenType", "refresh")
                .claim("username", username)
                .claim("role", role)
                .issuedAt(currentTime)
                .expiration(calculateExpirationOfTokenBasedonCurrentTime(currentTime, REFRESH_TOKEN_EXPIRED_HOUR))
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

    private Date calculateExpirationOfTokenBasedonCurrentTime(Date currentTime, int tokenExpireTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentTime);
        cal.add(Calendar.HOUR, tokenExpireTime);
        return cal.getTime();
    }

    private static void validateUserInfoAndCurrentTime(String username, String role, Date currentTime) {
        Assert.hasText(username, "username이 존재하지 않습니다!");
        Assert.hasText(role, "role이 존재하지 않습니다!");

        Assert.notNull(currentTime, "currentTime이 존재하지 않습니다!");
        Assert.isTrue(currentTime.getTime() > 0, "currentTime이 잘못되었습니다!");
    }
}
