package com.example.lifepremium.dto.response;

public record PremiumCalculateResponse(
        long annualPremium,
        long monthlyPremium
) {
}
