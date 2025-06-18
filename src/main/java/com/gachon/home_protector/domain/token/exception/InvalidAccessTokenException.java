package com.gachon.home_protector.domain.token.exception;

public class InvalidAccessTokenException extends RuntimeException {
    public InvalidAccessTokenException(String s) {
        super(s);
    }
}
