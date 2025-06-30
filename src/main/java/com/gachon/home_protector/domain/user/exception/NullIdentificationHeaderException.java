package com.gachon.home_protector.domain.user.exception;

public class NullIdentificationHeaderException extends RuntimeException {
    public NullIdentificationHeaderException(String s) {
        super(s);
    }
}
