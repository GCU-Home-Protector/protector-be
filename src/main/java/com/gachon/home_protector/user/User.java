package com.gachon.home_protector.user;

import com.gachon.home_protector.api.BaseEntity;
import com.gachon.home_protector.user.dto.RestUserLoginResponse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.gachon.home_protector.user.LoginUserType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private String password; // Rest : 사용자에게서 입력받은 비번, OAuth2 : provider type + provider id

    private String role;

    @Enumerated(EnumType.STRING)
    private LoginUserType loginUserType;

    @Builder
    private User(String userId, String password, String role, LoginUserType loginUserType) {
        this.userId = userId;
        this.password = password;
        this.role = role;
        this.loginUserType = loginUserType;
    }

    public static User createRestLoginUser(String userId, String password, String role) {
        return User.builder()
                .userId(userId)
                .password(password)
                .role(role)
                .loginUserType(REST_LOGIN_USER)
                .build();
    }

    public static User createOAuth2LoginUser(String userId, String password, String role) {
        return User.builder()
                .userId(userId)
                .password(password)
                .role(role)
                .loginUserType(OAUTH2_LOGIN_USER)
                .build();
    }

    public static User of (String userId, String password) {
        return User.builder()
                .userId(userId)
                .password(password)
                .build();
    }

    public RestUserLoginResponse toRestUserLoginResponse() {
        return new RestUserLoginResponse(id, userId, password, role);
    }
}
