package com.example.lifepremium.dto.response;

import java.time.OffsetDateTime;

public record ApiResponse<T>(String code, String message, T data, String timestamp) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("SUCCESS", null, data, OffsetDateTime.now().toString());
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(code, message, null, OffsetDateTime.now().toString());
    }
}
