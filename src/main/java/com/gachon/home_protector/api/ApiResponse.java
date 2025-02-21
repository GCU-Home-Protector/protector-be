package com.gachon.home_protector.api;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
public class ApiResponse <T> {

    private int code;

    private HttpStatus status;

    private String message;

    private T data;

    public ApiResponse(HttpStatus status, String message, T data) {
        this.code = status.value();
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data){
        return new ApiResponse<>(HttpStatus.OK, HttpStatus.OK.name(), data);
    }

    public static <T> ApiResponse<T> success(){
        return new ApiResponse<>(HttpStatus.OK, HttpStatus.OK.name(), null);
    }

    public static <T> ApiResponse<T> fail(HttpStatus status, String message){
        return new ApiResponse<>(status, message, null);
    }

    public static <T> ApiResponse<T> fail(HttpStatus status){
        return new ApiResponse<>(status, status.name(), null);
    }
}
