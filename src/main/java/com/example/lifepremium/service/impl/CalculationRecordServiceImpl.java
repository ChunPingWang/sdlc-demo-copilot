package com.example.lifepremium.service.impl;

import com.example.lifepremium.domain.CalculationRecord;
import com.example.lifepremium.domain.RateEntry;
import com.example.lifepremium.repository.CalculationRecordRepository;
import com.example.lifepremium.repository.ProductRepository;
import com.example.lifepremium.service.CalculationRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CalculationRecordServiceImpl implements CalculationRecordService {

    private final CalculationRecordRepository calculationRecordRepository;
    private final ProductRepository productRepository;

    public CalculationRecordServiceImpl(CalculationRecordRepository calculationRecordRepository,
                                         ProductRepository productRepository) {
        this.calculationRecordRepository = calculationRecordRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public void save(String productCode, RateEntry rateEntry, String agentId, int age, String gender,
                      int paymentPeriod, long insuredAmount, long annualPremium, long monthlyPremium) {
        var product = productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new IllegalStateException("Product not found: " + productCode));
        CalculationRecord record = new CalculationRecord(product, rateEntry, agentId, age, gender,
                paymentPeriod, insuredAmount, annualPremium, monthlyPremium);
        calculationRecordRepository.save(record);
    }
}
