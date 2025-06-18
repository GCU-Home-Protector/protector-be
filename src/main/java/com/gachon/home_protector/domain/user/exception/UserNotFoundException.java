package com.gachon.home_protector.domain.user.exception;


public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String s) {
        super(s);
    }
}
