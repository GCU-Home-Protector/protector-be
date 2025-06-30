package com.gachon.home_protector.domain.token.token;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 14440) // 4 hours, refreshToken : {userId} 형태 저장됨
@RequiredArgsConstructor
public class RefreshToken {

    @Id
    private final Long userId;
    private final String uuid;

    public static RefreshToken createRefreshToken(Long userId, String uuid) {
        return new RefreshToken(userId, uuid);
    }
}
