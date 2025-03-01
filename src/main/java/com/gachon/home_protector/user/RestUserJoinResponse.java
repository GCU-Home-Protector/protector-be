package com.gachon.home_protector.user;

import lombok.Getter;

@Getter
public class RestUserJoinResponse {

    private Long id;

    private String userId;

    private String role;

    private LoginUserType loginUserType;

    public RestUserJoinResponse(Long id, String userId, String role, LoginUserType loginUserType) {
        this.id = id;
        this.userId = userId;
        this.role = role;
        this.loginUserType = loginUserType;
    }
}
