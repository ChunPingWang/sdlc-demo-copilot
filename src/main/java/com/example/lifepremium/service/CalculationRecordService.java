package com.example.lifepremium.service;

import com.example.lifepremium.domain.RateEntry;

public interface CalculationRecordService {
    void save(String productCode, RateEntry rateEntry, String agentId, int age, String gender,
               int paymentPeriod, long insuredAmount, long annualPremium, long monthlyPremium);
}
