package com.example.lifepremium.exception;

public class AgeOutOfRangeException extends BusinessException {
    public AgeOutOfRangeException(String message) {
        super(ErrorCode.AGE_OUT_OF_RANGE, message);
    }
}
