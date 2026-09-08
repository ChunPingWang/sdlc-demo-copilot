package com.example.lifepremium.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PremiumCalculateRequest(
        @NotBlank String productCode,
        @NotNull Integer age,
        @NotBlank @Pattern(regexp = "M|F", message = "性別必須為 M 或 F") String gender,
        @NotNull Long insuredAmount,
        @NotNull Integer paymentPeriod
) {
}
