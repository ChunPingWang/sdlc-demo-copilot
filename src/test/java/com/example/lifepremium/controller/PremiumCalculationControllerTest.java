package com.example.lifepremium.controller;

import com.example.lifepremium.domain.Product;
import com.example.lifepremium.domain.RateEntry;
import com.example.lifepremium.domain.RateTableVersion;
import com.example.lifepremium.repository.ProductRepository;
import com.example.lifepremium.repository.RateEntryRepository;
import com.example.lifepremium.repository.RateTableVersionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// FR-PREMIUM-001：保費試算 Controller 整合測試
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PremiumCalculationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private RateTableVersionRepository rateTableVersionRepository;
    @Autowired
    private RateEntryRepository rateEntryRepository;

    @BeforeEach
    void setUp() {
        Product product = productRepository.save(new Product("LIFE-WL-01", "終身壽險"));
        RateTableVersion version = rateTableVersionRepository.save(
                new RateTableVersion(product, "v1", LocalDate.now()));
        rateEntryRepository.save(new RateEntry(version, 35, "M", 20, new BigDecimal("12.50")));
    }

    @Test
    void should_return_200_when_calculate_success() throws Exception {
        mockMvc.perform(post("/api/v1/premium/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productCode":"LIFE-WL-01","age":35,"gender":"M",
                                 "insuredAmount":10000000,"paymentPeriod":20}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.annualPremium").value(125000))
                .andExpect(jsonPath("$.data.monthlyPremium").value(10729));
    }

    @Test
    void should_return_400_when_age_out_of_range() throws Exception {
        mockMvc.perform(post("/api/v1/premium/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productCode":"LIFE-WL-01","age":99,"gender":"M",
                                 "insuredAmount":10000000,"paymentPeriod":20}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("AGE_OUT_OF_RANGE"));
    }

    @Test
    void should_return_400_when_amount_out_of_range() throws Exception {
        mockMvc.perform(post("/api/v1/premium/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productCode":"LIFE-WL-01","age":35,"gender":"M",
                                 "insuredAmount":60000000,"paymentPeriod":20}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("AMOUNT_OUT_OF_RANGE"));
    }

    @Test
    void should_return_400_when_payment_period_invalid() throws Exception {
        mockMvc.perform(post("/api/v1/premium/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productCode":"LIFE-WL-01","age":35,"gender":"M",
                                 "insuredAmount":10000000,"paymentPeriod":15}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_PAYMENT_PERIOD"));
    }

    @Test
    void should_return_404_when_rate_not_found() throws Exception {
        mockMvc.perform(post("/api/v1/premium/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productCode":"LIFE-WL-01","age":50,"gender":"M",
                                 "insuredAmount":10000000,"paymentPeriod":20}"""))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RATE_NOT_FOUND"));
    }
}
