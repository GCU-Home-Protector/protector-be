package com.gachon.home_protector.domain.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum AuthenticationErrorType {

    BAD_CREDENTIALS(BadCredentialsException.class, "ID 혹은 비밀번호가 잘못되었습니다."),
    USER_NOT_FOUND(UsernameNotFoundException.class, "유저가 존재하지 않습니다."),
    CREDENTIALS_EXPIRED(CredentialsExpiredException.class, "만료되었습니다!");

    private final Class<? extends AuthenticationException> authExceptionClass;
    private final String errorMessage;

    public static String findErrorMessageByException (AuthenticationException exception) {
        return Arrays.stream(AuthenticationErrorType.values())
                .filter(errorType -> {
                    Class<? extends AuthenticationException> authExceptionClass = errorType.getAuthExceptionClass();
                    return authExceptionClass.isAssignableFrom(exception.getClass());
                })
                .findFirst()
                .map(AuthenticationErrorType::getErrorMessage)
                .orElse("Unknown authentication error");
    }
}
