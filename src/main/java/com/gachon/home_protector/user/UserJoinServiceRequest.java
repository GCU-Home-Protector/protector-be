package com.gachon.home_protector.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserJoinServiceRequest {
    private String userId;
    private String password;

    @Builder
    private UserJoinServiceRequest(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public static UserJoinServiceRequest of (String userId, String password) {
        return UserJoinServiceRequest.builder()
                .userId(userId)
                .password(password)
                .build();
    }

    public User toUser() {
        return User.of(userId, password);
    }
}
