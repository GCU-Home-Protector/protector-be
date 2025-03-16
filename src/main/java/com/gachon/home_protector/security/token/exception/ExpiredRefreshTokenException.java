package com.gachon.home_protector.security.token.exception;

import org.springframework.security.core.AuthenticationException;

public class ExpiredRefreshTokenException extends AuthenticationException {
    public ExpiredRefreshTokenException(String s) {
        super(s);
    }
}
