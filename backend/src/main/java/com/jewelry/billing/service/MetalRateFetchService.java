package com.jewelry.billing.service;

import com.jewelry.billing.dto.DailyRateRequest;
import com.jewelry.billing.entity.MetalType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Optional automatic rate fetcher. Disabled by default — enable via app.rates.auto-fetch-enabled=true.
 * Managers can always override rates manually via POST /api/v1/rates.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.rates.auto-fetch-enabled", havingValue = "true")
public class MetalRateFetchService {

    private final DailyRateService dailyRateService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.rates.api-url:}")
    private String apiUrl;

    @Scheduled(cron = "${app.rates.fetch-cron}")
    public void fetchAndStoreRates() {
        if (apiUrl == null || apiUrl.isBlank()) {
            log.warn("Metal rate auto-fetch enabled but app.rates.api-url is not configured");
            return;
        }
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);
            if (response == null) {
                log.warn("Empty response from metal rate API");
                return;
            }
            applyFetchedRates(response);
            log.info("Daily metal rates fetched automatically from API");
        } catch (Exception ex) {
            log.error("Failed to fetch metal rates from API: {}", ex.getMessage());
        }
    }

    private void applyFetchedRates(Map<String, Object> response) {
        // Adapt this mapping to your chosen API provider's JSON shape
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rates = (List<Map<String, Object>>) response.get("rates");
        if (rates == null) {
            return;
        }
        for (Map<String, Object> entry : rates) {
            DailyRateRequest request = new DailyRateRequest();
            request.setMetalType(MetalType.valueOf(entry.get("metalType").toString()));
            request.setPurity(entry.get("purity").toString());
            request.setRatePerGram(new BigDecimal(entry.get("ratePerGram").toString()));
            dailyRateService.createRate(request, null, "API");
        }
    }
}
