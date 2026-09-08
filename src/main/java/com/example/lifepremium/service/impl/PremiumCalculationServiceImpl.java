package com.example.lifepremium.service.impl;

import com.example.lifepremium.domain.RateEntry;
import com.example.lifepremium.dto.request.PremiumCalculateRequest;
import com.example.lifepremium.dto.response.PremiumCalculateResponse;
import com.example.lifepremium.exception.AgeOutOfRangeException;
import com.example.lifepremium.exception.AmountOutOfRangeException;
import com.example.lifepremium.exception.InvalidPaymentPeriodException;
import com.example.lifepremium.service.CalculationRecordService;
import com.example.lifepremium.service.PremiumCalculationService;
import com.example.lifepremium.service.RateTableService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

@Service
public class PremiumCalculationServiceImpl implements PremiumCalculationService {

    // BR-003：允許的繳費年期
    private static final Set<Integer> ALLOWED_PAYMENT_PERIODS = Set.of(10, 20, 30, 99);
    // BR-002：保額範圍（元），100 萬 ~ 5,000 萬
    private static final long MIN_INSURED_AMOUNT = 1_000_000L;
    private static final long MAX_INSURED_AMOUNT = 50_000_000L;
    // BR-001：年齡範圍
    private static final int MIN_AGE = 0;
    private static final int MAX_AGE = 70;

    private static final BigDecimal MONTHLY_LOADING_FACTOR = new BigDecimal("1.03");

    private final RateTableService rateTableService;
    private final CalculationRecordService calculationRecordService;

    public PremiumCalculationServiceImpl(RateTableService rateTableService,
                                          CalculationRecordService calculationRecordService) {
        this.rateTableService = rateTableService;
        this.calculationRecordService = calculationRecordService;
    }

    @Override
    @Transactional
    public PremiumCalculateResponse calculate(PremiumCalculateRequest request, String agentId) {
        validateAge(request.age());
        validateAmount(request.insuredAmount());
        validatePaymentPeriod(request.paymentPeriod());

        RateEntry rateEntry = rateTableService.findRate(
                request.productCode(), request.age(), request.gender(), request.paymentPeriod());

        long annualPremium = calculateAnnualPremium(request.insuredAmount(), rateEntry.getRatePerThousand());
        long monthlyPremium = calculateMonthlyPremium(annualPremium);

        calculationRecordService.save(request.productCode(), rateEntry, agentId, request.age(), request.gender(),
                request.paymentPeriod(), request.insuredAmount(), annualPremium, monthlyPremium);

        return new PremiumCalculateResponse(annualPremium, monthlyPremium);
    }

    // BR-004：年繳保費 = ROUND(保額 ÷ 1000 × 費率)
    private long calculateAnnualPremium(long insuredAmount, BigDecimal ratePerThousand) {
        return BigDecimal.valueOf(insuredAmount)
                .divide(BigDecimal.valueOf(1000))
                .multiply(ratePerThousand)
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }

    // BR-005：月繳保費 = ROUND(年繳 ÷ 12 × 1.03)
    private long calculateMonthlyPremium(long annualPremium) {
        return BigDecimal.valueOf(annualPremium)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP)
                .multiply(MONTHLY_LOADING_FACTOR)
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }

    private void validateAge(int age) {
        if (age < MIN_AGE || age > MAX_AGE) {
            throw new AgeOutOfRangeException("年齡需介於 %d~%d 歲之間".formatted(MIN_AGE, MAX_AGE));
        }
    }

    private void validateAmount(long insuredAmount) {
        if (insuredAmount < MIN_INSURED_AMOUNT || insuredAmount > MAX_INSURED_AMOUNT) {
            throw new AmountOutOfRangeException("保額需介於 100 萬~5,000 萬元之間");
        }
    }

    private void validatePaymentPeriod(int paymentPeriod) {
        if (!ALLOWED_PAYMENT_PERIODS.contains(paymentPeriod)) {
            throw new InvalidPaymentPeriodException("繳費年期僅限 10/20/30/99 年");
        }
    }
}
