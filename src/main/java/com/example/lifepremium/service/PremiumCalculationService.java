package com.example.lifepremium.service;

import com.example.lifepremium.dto.request.PremiumCalculateRequest;
import com.example.lifepremium.dto.response.PremiumCalculateResponse;

public interface PremiumCalculationService {
    PremiumCalculateResponse calculate(PremiumCalculateRequest request, String agentId);
}
