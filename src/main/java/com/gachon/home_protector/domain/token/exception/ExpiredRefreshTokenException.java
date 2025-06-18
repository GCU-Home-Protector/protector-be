package com.gachon.home_protector.domain.token.exception;

import org.springframework.security.core.AuthenticationException;

public class ExpiredRefreshTokenException extends AuthenticationException {
    public ExpiredRefreshTokenException(String s) {
        super(s);
    }
}
