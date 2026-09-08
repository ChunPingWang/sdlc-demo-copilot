package com.example.lifepremium.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "rate_table_version",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "version_no"}))
public class RateTableVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "version_no", nullable = false, length = 10)
    private String versionNo;

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    protected RateTableVersion() {
        // JPA 需要無參建構子
    }

    public RateTableVersion(Product product, String versionNo, LocalDate effectiveDate) {
        this.product = product;
        this.versionNo = versionNo;
        this.effectiveDate = effectiveDate;
    }

    public UUID getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public String getVersionNo() {
        return versionNo;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }
}
