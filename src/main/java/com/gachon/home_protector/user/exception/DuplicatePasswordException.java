package com.gachon.home_protector.user.exception;

public class DuplicatePasswordException extends RuntimeException {
    public DuplicatePasswordException(String s) {
        super(s);
    }
}
