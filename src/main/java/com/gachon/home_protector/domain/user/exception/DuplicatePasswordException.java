package com.gachon.home_protector.domain.user.exception;

public class DuplicatePasswordException extends RuntimeException {
    public DuplicatePasswordException(String s) {
        super(s);
    }
}
