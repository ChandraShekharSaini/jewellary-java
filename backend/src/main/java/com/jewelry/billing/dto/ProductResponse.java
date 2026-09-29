package com.jewelry.billing.dto;

import com.jewelry.billing.entity.MetalType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private MetalType metalType;
    private String purity;
    private BigDecimal makingChargePerGram;
    private BigDecimal wastagePercent;
    private Boolean active;
}
