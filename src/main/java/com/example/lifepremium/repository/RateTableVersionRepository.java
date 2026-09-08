package com.example.lifepremium.repository;

import com.example.lifepremium.domain.Product;
import com.example.lifepremium.domain.RateTableVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RateTableVersionRepository extends JpaRepository<RateTableVersion, UUID> {
    Optional<RateTableVersion> findByProductAndVersionNo(Product product, String versionNo);
}
