package com.gachon.home_protector.security.token;

public class InvalidAccessTokenException extends RuntimeException {
    public InvalidAccessTokenException(String s) {
        super(s);
    }
}
