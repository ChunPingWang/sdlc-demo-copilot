package com.example.lifepremium.exception;

public class AmountOutOfRangeException extends BusinessException {
    public AmountOutOfRangeException(String message) {
        super(ErrorCode.AMOUNT_OUT_OF_RANGE, message);
    }
}
