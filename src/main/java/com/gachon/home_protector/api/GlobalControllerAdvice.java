package com.gachon.home_protector.api;

import com.gachon.home_protector.user.UserController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(assignableTypes = {
        UserController.class
})
public class GlobalControllerAdvice {

    @ExceptionHandler(BindException.class)
    public ErrorResponse bindExHandler(BindException e) {
        ObjectError objectError = e.getBindingResult().getAllErrors().get(0);
        String errorMessage = objectError.getDefaultMessage();

        log.info(errorMessage);

        return ErrorResponse.badRequestError(errorMessage);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse invalidInputFromUserHandler(Exception e) {
        String errorMessage = e.getMessage();

        log.info(errorMessage);

        return ErrorResponse.badRequestError(errorMessage);
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse otherExHandler(Exception e) {
        String errorMessage = e.getMessage();

        log.info(errorMessage);

        return ErrorResponse.internalServerError(errorMessage);
    }
}
