package com.example.lifepremium.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "rate_entry",
        uniqueConstraints = @UniqueConstraint(columnNames = {"rate_table_version_id", "age", "gender", "payment_period"}))
public class RateEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rate_table_version_id", nullable = false)
    private RateTableVersion rateTableVersion;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false, length = 1)
    private String gender;

    @Column(name = "payment_period", nullable = false)
    private Integer paymentPeriod;

    @Column(name = "rate_per_thousand", nullable = false, precision = 10, scale = 2)
    private BigDecimal ratePerThousand;

    protected RateEntry() {
        // JPA 需要無參建構子
    }

    public RateEntry(RateTableVersion rateTableVersion, Integer age, String gender,
                      Integer paymentPeriod, BigDecimal ratePerThousand) {
        this.rateTableVersion = rateTableVersion;
        this.age = age;
        this.gender = gender;
        this.paymentPeriod = paymentPeriod;
        this.ratePerThousand = ratePerThousand;
    }

    public UUID getId() {
        return id;
    }

    public RateTableVersion getRateTableVersion() {
        return rateTableVersion;
    }

    public Integer getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public Integer getPaymentPeriod() {
        return paymentPeriod;
    }

    public BigDecimal getRatePerThousand() {
        return ratePerThousand;
    }
}
