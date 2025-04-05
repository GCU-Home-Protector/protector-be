package com.gachon.home_protector.token.exception;

import org.springframework.security.core.AuthenticationException;

public class ExpiredRefreshTokenException extends AuthenticationException {
    public ExpiredRefreshTokenException(String s) {
        super(s);
    }
}
