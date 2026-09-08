package com.example.lifepremium.exception;

public class InvalidPaymentPeriodException extends BusinessException {
    public InvalidPaymentPeriodException(String message) {
        super(ErrorCode.INVALID_PAYMENT_PERIOD, message);
    }
}
