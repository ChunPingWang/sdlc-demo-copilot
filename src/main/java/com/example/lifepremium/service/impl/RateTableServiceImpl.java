package com.example.lifepremium.service.impl;

import com.example.lifepremium.domain.RateEntry;
import com.example.lifepremium.repository.RateEntryRepository;
import com.example.lifepremium.exception.RateNotFoundException;
import com.example.lifepremium.service.RateTableService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RateTableServiceImpl implements RateTableService {

    private final RateEntryRepository rateEntryRepository;

    public RateTableServiceImpl(RateEntryRepository rateEntryRepository) {
        this.rateEntryRepository = rateEntryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public RateEntry findRate(String productCode, int age, String gender, int paymentPeriod) {
        List<RateEntry> candidates = rateEntryRepository.findCandidates(productCode, age, gender, paymentPeriod);
        if (candidates.isEmpty()) {
            throw new RateNotFoundException(
                    "查無費率：productCode=%s, age=%d, gender=%s, paymentPeriod=%d"
                            .formatted(productCode, age, gender, paymentPeriod));
        }
        return candidates.get(0);
    }
}
