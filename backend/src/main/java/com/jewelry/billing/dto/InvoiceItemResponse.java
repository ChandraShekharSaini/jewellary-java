package com.jewelry.billing.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class InvoiceItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal weightGrams;
    private BigDecimal rateApplied;
    private BigDecimal makingApplied;
    private BigDecimal metalAmount;
    private BigDecimal makingAmount;
    private BigDecimal lineSubtotal;
    private BigDecimal taxAmount;
    private BigDecimal lineTotal;
    private Long dailyRateId;
}
