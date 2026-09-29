package com.jewelry.billing.service;

import com.jewelry.billing.dto.DailyRateRequest;
import com.jewelry.billing.dto.DailyRateResponse;
import com.jewelry.billing.entity.DailyRate;
import com.jewelry.billing.exception.ResourceNotFoundException;
import com.jewelry.billing.repository.DailyRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyRateService {

    private final DailyRateRepository dailyRateRepository;

    @Transactional
    public DailyRateResponse createRate(DailyRateRequest request, Long userId, String source) {
        DailyRate rate = DailyRate.builder()
                .metalType(request.getMetalType())
                .purity(request.getPurity())
                .ratePerGram(request.getRatePerGram())
                .source(source)
                .createdBy(userId)
                .build();
        return toResponse(dailyRateRepository.save(rate));
    }

    @Transactional(readOnly = true)
    public List<DailyRateResponse> getLatestRates() {
        return dailyRateRepository.findAllLatestRates().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DailyRate getActiveRate(com.jewelry.billing.entity.MetalType metalType, String purity) {
        return dailyRateRepository.findFirstByMetalTypeAndPurityOrderByUpdatedAtDesc(metalType, purity)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No rate found for " + metalType + " " + purity + ". Please update daily rates."));
    }

    @Transactional(readOnly = true)
    public List<DailyRateResponse> getRateHistory(com.jewelry.billing.entity.MetalType metalType) {
        return dailyRateRepository.findByMetalTypeOrderByUpdatedAtDesc(metalType).stream()
                .map(this::toResponse)
                .toList();
    }

    private DailyRateResponse toResponse(DailyRate rate) {
        return DailyRateResponse.builder()
                .id(rate.getId())
                .metalType(rate.getMetalType())
                .purity(rate.getPurity())
                .ratePerGram(rate.getRatePerGram())
                .updatedAt(rate.getUpdatedAt())
                .source(rate.getSource())
                .build();
    }
}
