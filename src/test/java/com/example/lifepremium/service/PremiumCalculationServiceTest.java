package com.example.lifepremium.service;

import com.example.lifepremium.domain.RateEntry;
import com.example.lifepremium.dto.request.PremiumCalculateRequest;
import com.example.lifepremium.dto.response.PremiumCalculateResponse;
import com.example.lifepremium.exception.AgeOutOfRangeException;
import com.example.lifepremium.exception.AmountOutOfRangeException;
import com.example.lifepremium.exception.InvalidPaymentPeriodException;
import com.example.lifepremium.exception.RateNotFoundException;
import com.example.lifepremium.service.impl.PremiumCalculationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// FR-PREMIUM-001：保費試算 Service 單元測試
@ExtendWith(MockitoExtension.class)
class PremiumCalculationServiceTest {

    @Mock
    private RateTableService rateTableService;
    @Mock
    private CalculationRecordService calculationRecordService;

    private PremiumCalculationServiceImpl service;

    private PremiumCalculationServiceImpl newService() {
        return new PremiumCalculationServiceImpl(rateTableService, calculationRecordService);
    }

    // BR-004/BR-005：年齡 35 / 男性 / 保額 1000 萬 / 20 年期 / 費率 12.50 → 年繳 125,000、月繳 10,729
    @Test
    void calculate_should_return_correct_annual_and_monthly_premium() {
        service = newService();
        RateEntry rateEntry = new RateEntry(null, 35, "M", 20, new BigDecimal("12.50"));
        when(rateTableService.findRate("LIFE-WL-01", 35, "M", 20)).thenReturn(rateEntry);

        PremiumCalculateRequest request = new PremiumCalculateRequest("LIFE-WL-01", 35, "M", 10_000_000L, 20);
        PremiumCalculateResponse response = service.calculate(request, "agent-001");

        assertThat(response.annualPremium()).isEqualTo(125_000L);
        assertThat(response.monthlyPremium()).isEqualTo(10_729L);
        verify(calculationRecordService).save("LIFE-WL-01", rateEntry, "agent-001", 35, "M", 20,
                10_000_000L, 125_000L, 10_729L);
    }

    // BR-001：年齡邊界
    @Test
    void calculate_should_throw_AgeOutOfRangeException_when_age_over_70() {
        service = newService();
        PremiumCalculateRequest request = new PremiumCalculateRequest("LIFE-WL-01", 71, "M", 10_000_000L, 20);

        assertThatThrownBy(() -> service.calculate(request, null))
                .isInstanceOf(AgeOutOfRangeException.class);
    }

    // BR-002：保額邊界
    @Test
    void calculate_should_throw_AmountOutOfRangeException_when_amount_over_limit() {
        service = newService();
        PremiumCalculateRequest request = new PremiumCalculateRequest("LIFE-WL-01", 35, "M", 60_000_000L, 20);

        assertThatThrownBy(() -> service.calculate(request, null))
                .isInstanceOf(AmountOutOfRangeException.class);
    }

    // BR-003：繳費年期限制
    @Test
    void calculate_should_throw_InvalidPaymentPeriodException_when_period_not_allowed() {
        service = newService();
        PremiumCalculateRequest request = new PremiumCalculateRequest("LIFE-WL-01", 35, "M", 10_000_000L, 15);

        assertThatThrownBy(() -> service.calculate(request, null))
                .isInstanceOf(InvalidPaymentPeriodException.class);
    }

    // 查無費率
    @Test
    void calculate_should_throw_RateNotFoundException_when_rate_missing() {
        service = newService();
        when(rateTableService.findRate(anyString(), anyInt(), anyString(), anyInt()))
                .thenThrow(new RateNotFoundException("查無費率"));

        PremiumCalculateRequest request = new PremiumCalculateRequest("LIFE-WL-01", 50, "M", 10_000_000L, 20);

        assertThatThrownBy(() -> service.calculate(request, null))
                .isInstanceOf(RateNotFoundException.class);
    }
}
