package com.gachon.home_protector.security.token.exception;

public class InvalidAccessTokenException extends RuntimeException {
    public InvalidAccessTokenException(String s) {
        super(s);
    }
}
