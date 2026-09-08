package com.example.lifepremium.controller;

import com.example.lifepremium.dto.request.PremiumCalculateRequest;
import com.example.lifepremium.dto.response.ApiResponse;
import com.example.lifepremium.dto.response.PremiumCalculateResponse;
import com.example.lifepremium.service.PremiumCalculationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/premium")
public class PremiumCalculationController {

    private final PremiumCalculationService premiumCalculationService;

    public PremiumCalculationController(PremiumCalculationService premiumCalculationService) {
        this.premiumCalculationService = premiumCalculationService;
    }

    @PostMapping("/calculate")
    public ApiResponse<PremiumCalculateResponse> calculate(
            @Valid @RequestBody PremiumCalculateRequest request,
            @RequestHeader(value = "X-Agent-Id", required = false) String agentId) {
        return ApiResponse.success(premiumCalculationService.calculate(request, agentId));
    }
}
