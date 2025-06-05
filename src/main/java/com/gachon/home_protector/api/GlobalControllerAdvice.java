package com.gachon.home_protector.api;

import com.gachon.home_protector.music.MusicController;
import com.gachon.home_protector.music.exception.FavoriteMusicNotFoundException;
import com.gachon.home_protector.music.exception.MusicNotFoundException;
import com.gachon.home_protector.music.exception.ai.BadRequestFromAIException;
import com.gachon.home_protector.music.exception.ai.InternalServerErrorFromAIException;
import com.gachon.home_protector.user.exception.InvalidIdentificationTokenException;
import com.gachon.home_protector.user.UserController;
import com.gachon.home_protector.user.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(assignableTypes = {
        UserController.class,
        MusicController.class
})
public class GlobalControllerAdvice {

    // 400
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            BindException.class,
            MethodArgumentNotValidException.class
    })
    public ErrorResponse bindExHandler(Exception e) {
        String errorMessage = extractBindingResult(e).getAllErrors().get(0).getDefaultMessage();
        log.warn(errorMessage);
        return ErrorResponse.badRequestError(errorMessage);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            IllegalArgumentException.class,
            InvalidIdentificationTokenException.class,
            DuplicateUserIdException.class,
            DuplicatePasswordException.class
    })
    public ErrorResponse badRequestExHandler(Exception e) {
        String errorMessage = e.getMessage();

        log.warn(errorMessage);

        return ErrorResponse.badRequestError(errorMessage);
    }


    // 401
    // Protector-Identification, Custom header 자체가 없을 경우 발생, 만약 header 사용할 일이 또 생길 경우 예외 다르게 처리 고민해보자
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(NullIdentificationHeaderException.class)
    public ErrorResponse unauthorizedExHandler(Exception e) {
        String errorMessage = e.getMessage();
        log.warn(errorMessage);
        return ErrorResponse.unauthorizedError(errorMessage);
    }



    // 404
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            UserNotFoundException.class,
            MusicNotFoundException.class,
            FavoriteMusicNotFoundException.class
    })
    public ErrorResponse notFoundExHandler(Exception e) {
        String errorMessage = e.getMessage();

        log.warn(errorMessage);

        return ErrorResponse.notFoundError(errorMessage);
    }


    // 500
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({
            EmptyIdentificationHeaderException.class,
            BadRequestFromAIException.class,
            InternalServerErrorFromAIException.class,
            Exception.class
    })
    public ErrorResponse serverExHandler(Exception e) {
        String errorMessage = e.getMessage();
        log.error(errorMessage);
        return ErrorResponse.internalServerError(errorMessage);
    }

    private BindingResult extractBindingResult(Exception e) {
        if (e instanceof BindException ex) return ex.getBindingResult();
        if (e instanceof MethodArgumentNotValidException ex) return ex.getBindingResult();
        return null;
    }
}
