package com.gachon.home_protector.domain.music.exception;

public class FavoriteMusicNotFoundException extends RuntimeException {
    public FavoriteMusicNotFoundException(String message) {
        super(message);
    }
}
