package com.example.lifepremium.exception;

public class RateNotFoundException extends BusinessException {
    public RateNotFoundException(String message) {
        super(ErrorCode.RATE_NOT_FOUND, message);
    }
}
