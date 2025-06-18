package com.gachon.home_protector.domain.user.dto.login;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RestUserLoginResponse {

    private Long id;
    private String userId;
    private String password;
    private String role;

    public void removePassword() {
        this.password = null;
    }
}
