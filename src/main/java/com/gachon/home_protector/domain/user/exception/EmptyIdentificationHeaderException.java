package com.gachon.home_protector.domain.user.exception;

public class EmptyIdentificationHeaderException extends RuntimeException {
    public EmptyIdentificationHeaderException(String s) {
        super(s);
    }
}
