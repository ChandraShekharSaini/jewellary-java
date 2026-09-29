package com.jewelry.billing.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BillingCalculatorTest {

    private final BillingCalculator calculator = new BillingCalculator();

    @Test
    void calculatesLineWithGstAndNoWastage() {
        var product = com.jewelry.billing.entity.Product.builder()
                .makingChargePerGram(new BigDecimal("450.0000"))
                .wastagePercent(null)
                .build();

        var result = calculator.calculateLine(
                product,
                new BigDecimal("10.5000"),
                new BigDecimal("6850.0000"),
                new BigDecimal("3.00"));

        assertEquals(new BigDecimal("76612.50"), result.getMetalAmount());
        assertEquals(new BigDecimal("4725.00"), result.getMakingAmount());
        assertEquals(new BigDecimal("2439.38"), result.getTaxAmount());
        assertEquals(new BigDecimal("83776.88"), result.getLineTotal());
    }
}
