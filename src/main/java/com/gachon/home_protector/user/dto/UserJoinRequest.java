package com.gachon.home_protector.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserJoinRequest {
    private String userId;
    private String password;

    @Builder
    private UserJoinRequest(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public static UserJoinRequest of (String userId, String password) {
        return UserJoinRequest.builder()
                .userId(userId)
                .password(password)
                .build();
    }

    public UserJoinServiceRequest toServiceRequest() {
        return UserJoinServiceRequest.of(userId, password);
    }
}
