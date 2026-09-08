package com.example.lifepremium.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST),
    AGE_OUT_OF_RANGE(HttpStatus.BAD_REQUEST),
    AMOUNT_OUT_OF_RANGE(HttpStatus.BAD_REQUEST),
    INVALID_PAYMENT_PERIOD(HttpStatus.BAD_REQUEST),
    RATE_NOT_FOUND(HttpStatus.NOT_FOUND);

    private final HttpStatus httpStatus;

    ErrorCode(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }
}
