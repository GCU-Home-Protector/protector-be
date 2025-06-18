package com.gachon.home_protector.domain.user.exception;

public class InvalidIdentificationTokenException extends RuntimeException {

    public InvalidIdentificationTokenException(String s) {
        super(s);
    }
}
