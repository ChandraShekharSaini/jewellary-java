package com.jewelry.billing.dto;

import com.jewelry.billing.entity.MetalType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {
    @NotBlank
    private String name;

    @NotNull
    private MetalType metalType;

    @NotBlank
    private String purity;

    @NotNull
    @DecimalMin("0")
    private BigDecimal makingChargePerGram;

    @DecimalMin("0")
    private BigDecimal wastagePercent;
}
