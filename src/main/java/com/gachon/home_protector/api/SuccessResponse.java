package com.gachon.home_protector.api;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
public class SuccessResponse<T> {

    private int code;

    private HttpStatus status;

    private String message;

    private T data;

    public SuccessResponse(HttpStatus status, String message, T data) {
        this.code = status.value();
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> SuccessResponse<T> success(T data){
        return new SuccessResponse<>(HttpStatus.OK, HttpStatus.OK.name(), data);
    }

    public static <T> SuccessResponse<T> success(){
        return new SuccessResponse<>(HttpStatus.OK, HttpStatus.OK.name(), null);
    }
}
