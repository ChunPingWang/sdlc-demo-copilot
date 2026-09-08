package com.example.lifepremium.repository;

import com.example.lifepremium.domain.RateEntry;
import com.example.lifepremium.domain.RateTableVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RateEntryRepository extends JpaRepository<RateEntry, UUID> {

    boolean existsByRateTableVersionAndAgeAndGenderAndPaymentPeriod(
            RateTableVersion rateTableVersion, Integer age, String gender, Integer paymentPeriod);

    @Query("""
            select re from RateEntry re
            where re.rateTableVersion.product.productCode = :productCode
              and re.age = :age
              and re.gender = :gender
              and re.paymentPeriod = :paymentPeriod
            order by re.rateTableVersion.effectiveDate desc
            """)
    List<RateEntry> findCandidates(@Param("productCode") String productCode,
                                    @Param("age") int age,
                                    @Param("gender") String gender,
                                    @Param("paymentPeriod") int paymentPeriod);
}
