package com.example.lifepremium.service;

import com.example.lifepremium.domain.RateEntry;

public interface RateTableService {
    RateEntry findRate(String productCode, int age, String gender, int paymentPeriod);
}
