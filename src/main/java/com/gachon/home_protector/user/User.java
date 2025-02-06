package com.gachon.home_protector.user;

import com.gachon.home_protector.api.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private String password;

    @Builder
    private User(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public static User of (String userId, String password) {
        return User.builder()
                .userId(userId)
                .password(password)
                .build();
    }
}
