package com.example.lifepremium.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "calculation_record")
public class CalculationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rate_entry_id", nullable = false)
    private RateEntry rateEntry;

    @Column(name = "agent_id", length = 64)
    private String agentId;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false, length = 1)
    private String gender;

    @Column(name = "payment_period", nullable = false)
    private Integer paymentPeriod;

    @Column(name = "insured_amount", nullable = false)
    private Long insuredAmount;

    @Column(name = "annual_premium", nullable = false)
    private Long annualPremium;

    @Column(name = "monthly_premium", nullable = false)
    private Long monthlyPremium;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected CalculationRecord() {
        // JPA 需要無參建構子
    }

    public CalculationRecord(Product product, RateEntry rateEntry, String agentId, Integer age, String gender,
                              Integer paymentPeriod, Long insuredAmount, Long annualPremium, Long monthlyPremium) {
        this.product = product;
        this.rateEntry = rateEntry;
        this.agentId = agentId;
        this.age = age;
        this.gender = gender;
        this.paymentPeriod = paymentPeriod;
        this.insuredAmount = insuredAmount;
        this.annualPremium = annualPremium;
        this.monthlyPremium = monthlyPremium;
        this.createdAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public RateEntry getRateEntry() {
        return rateEntry;
    }

    public String getAgentId() {
        return agentId;
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

    public Long getInsuredAmount() {
        return insuredAmount;
    }

    public Long getAnnualPremium() {
        return annualPremium;
    }

    public Long getMonthlyPremium() {
        return monthlyPremium;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
