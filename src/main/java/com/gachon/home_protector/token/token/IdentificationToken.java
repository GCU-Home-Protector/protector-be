package com.gachon.home_protector.token.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "identificationToken", timeToLive = 300)
@RequiredArgsConstructor
public class IdentificationToken {

    @Id
    private final String uuid;

    public static IdentificationToken createIdentificationToken(String uuid) {
        return new IdentificationToken(uuid);
    }

}
