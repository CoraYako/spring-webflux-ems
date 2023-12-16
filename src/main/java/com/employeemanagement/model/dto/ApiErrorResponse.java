package com.employeemanagement.model.dto;

import com.employeemanagement.model.HttpErrorCode;

import java.time.LocalDateTime;

public record ApiErrorResponse(LocalDateTime timestamp, String message, HttpErrorCode errorCode) {
    public static ApiErrorResponseBuilder builder() {
        return new ApiErrorResponseBuilder();
    }

    public static class ApiErrorResponseBuilder {
        private LocalDateTime timestamp;
        private String message;
        private HttpErrorCode errorCode;

        public ApiErrorResponseBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ApiErrorResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public ApiErrorResponseBuilder errorCode(HttpErrorCode errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public ApiErrorResponse build() {
            return new ApiErrorResponse(timestamp, message, errorCode);
        }
    }
}
