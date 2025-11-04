package com.arka.MSAuthentication.infrastructure.Exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseStatus(org.springframework.http.HttpStatus.CONFLICT)

    public String Exception(Exception ex) {
        return ex.getMessage();
    }
}
