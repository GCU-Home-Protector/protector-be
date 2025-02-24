package com.gachon.home_protector.security.token;


import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 14440) // 4 hours
public class RefreshToken {

    @Id
    private Long userId;

    private String uuid;

    public RefreshToken(Long userId, String uuid) {
        this.userId = userId;
        this.uuid = uuid;
    }

    public static RefreshToken createRefreshToken(Long userId) {
        String uuid = UUID.randomUUID().toString();
        return new RefreshToken(userId, uuid);
    }
}