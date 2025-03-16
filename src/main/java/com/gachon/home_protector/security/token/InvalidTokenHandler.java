package com.gachon.home_protector.security.token;

import com.gachon.home_protector.api.ErrorResponse;
import com.gachon.home_protector.security.token.controller.ReIssueController;
import com.gachon.home_protector.security.token.exception.ExpiredRefreshTokenException;
import com.gachon.home_protector.security.token.exception.InvalidAccessTokenException;
import com.gachon.home_protector.security.token.exception.TokenNotFoundException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(assignableTypes = {
        ReIssueController.class
})
public class InvalidTokenHandler {

    @ExceptionHandler(TokenNotFoundException.class)
    public ErrorResponse tokenNotFoundExHandler (TokenNotFoundException exception) {
        log.error(exception.getMessage());
        return ErrorResponse.unauthorizedError(exception.getMessage());
    }

    @ExceptionHandler(ExpiredRefreshTokenException.class)
    public ErrorResponse tokenNotFoundExHandler (ExpiredRefreshTokenException exception) {
        log.info(exception.getMessage());
        return ErrorResponse.unauthorizedError(exception.getMessage());
    }

    @ExceptionHandler(InvalidAccessTokenException.class)
    public ErrorResponse invalidAccessTokenExHandler (InvalidAccessTokenException exception) {
        log.error(exception.getMessage());
        return ErrorResponse.badRequestError(exception.getMessage());
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ErrorResponse malFormedJwtExHandler (MalformedJwtException exception) {
        log.error(exception.getMessage());
        return ErrorResponse.badRequestError(exception.getMessage());
    }
}
