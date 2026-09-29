package com.jewelry.billing.controller;

import com.jewelry.billing.dto.DailyRateRequest;
import com.jewelry.billing.dto.DailyRateResponse;
import com.jewelry.billing.entity.MetalType;
import com.jewelry.billing.entity.User;
import com.jewelry.billing.repository.UserRepository;
import com.jewelry.billing.service.DailyRateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rates")
@RequiredArgsConstructor
public class DailyRateController {

    private final DailyRateService dailyRateService;
    private final UserRepository userRepository;

    @GetMapping
    public List<DailyRateResponse> getLatestRates() {
        return dailyRateService.getLatestRates();
    }

    @GetMapping("/history/{metalType}")
    public List<DailyRateResponse> getHistory(@PathVariable MetalType metalType) {
        return dailyRateService.getRateHistory(metalType);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public DailyRateResponse createRate(@Valid @RequestBody DailyRateRequest request, Authentication auth) {
        Long userId = resolveUserId(auth.getName());
        return dailyRateService.createRate(request, userId, "MANUAL");
    }

    private Long resolveUserId(String username) {
        return userRepository.findByUsername(username).map(User::getId).orElse(null);
    }
}
