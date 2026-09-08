package com.example.lifepremium.repository;

import com.example.lifepremium.domain.Product;
import com.example.lifepremium.domain.RateEntry;
import com.example.lifepremium.domain.RateTableVersion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// FR-PREMIUM-001：費率查詢 Repository 測試
@DataJpaTest
class RateEntryRepositoryTest {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private RateTableVersionRepository rateTableVersionRepository;
    @Autowired
    private RateEntryRepository rateEntryRepository;

    @Test
    void findCandidates_should_return_rate_when_exists() {
        Product product = productRepository.save(new Product("LIFE-WL-01", "終身壽險"));
        RateTableVersion version = rateTableVersionRepository.save(
                new RateTableVersion(product, "v1", LocalDate.now()));
        rateEntryRepository.save(new RateEntry(version, 35, "M", 20, new BigDecimal("12.50")));

        List<RateEntry> results = rateEntryRepository.findCandidates("LIFE-WL-01", 35, "M", 20);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getRatePerThousand()).isEqualByComparingTo("12.50");
    }

    @Test
    void findCandidates_should_return_empty_when_not_exists() {
        assertThat(rateEntryRepository.findCandidates("LIFE-WL-01", 99, "M", 20)).isEmpty();
    }

    @Test
    void existsByRateTableVersionAndAgeAndGenderAndPaymentPeriod_should_detect_duplicate() {
        Product product = productRepository.save(new Product("LIFE-WL-01", "終身壽險"));
        RateTableVersion version = rateTableVersionRepository.save(
                new RateTableVersion(product, "v1", LocalDate.now()));
        rateEntryRepository.save(new RateEntry(version, 35, "M", 20, new BigDecimal("12.50")));

        boolean exists = rateEntryRepository.existsByRateTableVersionAndAgeAndGenderAndPaymentPeriod(
                version, 35, "M", 20);

        assertThat(exists).isTrue();
    }
}
