package com.jewelry.billing.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InvoiceLineRequest {
    @NotNull
    private Long productId;

    @NotNull
    @DecimalMin(value = "0.0001")
    private BigDecimal weightGrams;
}
