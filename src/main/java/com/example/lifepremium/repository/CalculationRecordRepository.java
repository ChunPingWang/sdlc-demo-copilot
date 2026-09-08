package com.example.lifepremium.repository;

import com.example.lifepremium.domain.CalculationRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CalculationRecordRepository extends JpaRepository<CalculationRecord, UUID> {
    List<CalculationRecord> findByAgentIdOrderByCreatedAtDesc(String agentId);
}
