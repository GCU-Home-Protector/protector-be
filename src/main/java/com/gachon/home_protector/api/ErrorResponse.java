package com.gachon.home_protector.api;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse {

    private final int code;

    private final HttpStatus status;

    private final String message;

    @Builder
    private ErrorResponse(HttpStatus status, String message) {
        this.code = status.value();
        this.status = status;
        this.message = message;
    }

    public static ErrorResponse unauthorizedError (String message) {
        return ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message(message)
                .build();
    }

    public static ErrorResponse forbiddenError (String message) {
        return ErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN)
                .message(message)
                .build();
    }

    public static ErrorResponse badRequestError (String message) {
        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(message)
                .build();
    }

    public static ErrorResponse internalServerError (String message) {
        return ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(message)
                .build();
    }
}
