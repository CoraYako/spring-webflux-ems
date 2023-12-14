package com.employeemanagement.controller;

import com.employeemanagement.model.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

import static com.employeemanagement.model.HttpErrorCode.MISSING_REQUIRED_ARGUMENT;
import static com.employeemanagement.model.HttpErrorCode.RESOURCE_NOT_FOUND;

@RestControllerAdvice
public class RestHandlerException {
    @ExceptionHandler(value = RuntimeException.class)
    protected ResponseEntity<Object> handleRunTimeException(RuntimeException ex) {
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .errorCode(RESOURCE_NOT_FOUND)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiErrorResponse);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    protected ResponseEntity<Object> handleRunTimeException(IllegalArgumentException ex) {
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .errorCode(MISSING_REQUIRED_ARGUMENT)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiErrorResponse);
    }
}
