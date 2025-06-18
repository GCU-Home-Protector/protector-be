package com.gachon.home_protector.domain.user.exception;

public class DuplicateUserIdException extends RuntimeException {
    public DuplicateUserIdException(String s) {
        super(s);
    }
}
