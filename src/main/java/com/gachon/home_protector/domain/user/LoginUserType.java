package com.gachon.home_protector.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LoginUserType {

    REST_LOGIN_USER, OAUTH2_LOGIN_USER;

    public boolean isRestLoginUser(LoginUserType loginUserType) {
        return loginUserType == REST_LOGIN_USER;
    }

    public boolean isOAuth2LoginUser(LoginUserType loginUserType) {
        return loginUserType == OAUTH2_LOGIN_USER;
    }
}
