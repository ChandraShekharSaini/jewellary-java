package com.jewelry.billing.dto;

import com.jewelry.billing.entity.MetalType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class DailyRateResponse {
    private Long id;
    private MetalType metalType;
    private String purity;
    private BigDecimal ratePerGram;
    private Instant updatedAt;
    private String source;
}
