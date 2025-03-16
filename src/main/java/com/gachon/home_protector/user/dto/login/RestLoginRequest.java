package com.gachon.home_protector.user.dto.login;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RestLoginRequest {

    private String userId;
    private String password;

    @Builder
    private RestLoginRequest(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public static RestLoginRequest of(String userId, String password) {
        return RestLoginRequest.builder()
                .userId(userId)
                .password(password)
                .build();
    }
}
