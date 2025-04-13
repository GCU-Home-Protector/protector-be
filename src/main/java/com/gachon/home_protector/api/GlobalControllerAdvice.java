package com.gachon.home_protector.api;

import com.gachon.home_protector.user.UserController;
import com.gachon.home_protector.user.exception.EmptyIdentificationHeaderException;
import com.gachon.home_protector.user.exception.NullIdentificationHeaderException;
import com.gachon.home_protector.user.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(assignableTypes = {
        UserController.class
})
public class GlobalControllerAdvice {

    // 400
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ErrorResponse bindExHandler(BindException e) {
        ObjectError objectError = e.getBindingResult().getAllErrors().get(0);
        String errorMessage = objectError.getDefaultMessage();

        log.info(errorMessage);

        return ErrorResponse.badRequestError(errorMessage);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse invalidInputFromUserHandler(Exception e) {
        String errorMessage = e.getMessage();

        log.info(errorMessage);

        return ErrorResponse.badRequestError(errorMessage);
    }



    // 401
    // Protector-Identification, Custom header 자체가 없을 경우 발생, 만약 header 사용할 일이 또 생길 경우 예외 다르게 처리 고민해보자
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(NullIdentificationHeaderException.class)
    public ErrorResponse headerNotFoundExHandler(Exception e) {
        String errorMessage = e.getMessage();
        log.warn(errorMessage);
        return ErrorResponse.unauthorizedError(errorMessage);
    }



    // 404
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ErrorResponse userNotFoundExHandler(Exception e) {
        String errorMessage = e.getMessage();

        log.info(errorMessage);

        return ErrorResponse.notFoundError(errorMessage);
    }


    // 500
    // Custom header은 존재하지만, 내부 값이 없을 경우
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(EmptyIdentificationHeaderException.class)
    public ErrorResponse blankHeaderExHandler(Exception e) {
        String errorMessage = e.getMessage();
        log.error(errorMessage);
        return ErrorResponse.internalServerError(errorMessage);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse otherExHandler(Exception e) {
        String errorMessage = e.getMessage();

        log.error(errorMessage);

        return ErrorResponse.internalServerError(errorMessage);
    }
}
