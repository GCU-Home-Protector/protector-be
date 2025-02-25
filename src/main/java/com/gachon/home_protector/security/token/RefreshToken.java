package com.gachon.home_protector.security.token;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 14440) // 4 hours, refreshToken : {userId} 형태 저장됨
@RequiredArgsConstructor
public class RefreshToken {

    @Id
    private final Long userId;
    private final String uuid;

    public static RefreshToken createRefreshToken(Long userId) {
        String uuid = UUID.randomUUID().toString();
        return new RefreshToken(userId, uuid);
    }
}