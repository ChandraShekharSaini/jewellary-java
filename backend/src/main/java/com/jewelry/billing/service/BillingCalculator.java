package com.jewelry.billing.service;

import com.jewelry.billing.entity.InvoiceItem;
import com.jewelry.billing.entity.Product;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class BillingCalculator {

    private static final int MONEY_SCALE = 2;
    private static final int RATE_SCALE = 4;
    private static final RoundingMode ROUND = RoundingMode.HALF_UP;

    public LineCalculation calculateLine(
            Product product,
            BigDecimal weightGrams,
            BigDecimal ratePerGram,
            BigDecimal gstPercent) {

        BigDecimal effectiveWeight = applyWastage(weightGrams, product.getWastagePercent());
        BigDecimal metalAmount = effectiveWeight
                .multiply(ratePerGram)
                .setScale(MONEY_SCALE, ROUND);
        BigDecimal makingAmount = effectiveWeight
                .multiply(product.getMakingChargePerGram())
                .setScale(MONEY_SCALE, ROUND);
        BigDecimal lineSubtotal = metalAmount.add(makingAmount);
        BigDecimal taxAmount = lineSubtotal
                .multiply(gstPercent)
                .divide(BigDecimal.valueOf(100), MONEY_SCALE, ROUND);
        BigDecimal lineTotal = lineSubtotal.add(taxAmount);

        return LineCalculation.builder()
                .weightGrams(weightGrams.setScale(RATE_SCALE, ROUND))
                .effectiveWeight(effectiveWeight.setScale(RATE_SCALE, ROUND))
                .rateApplied(ratePerGram.setScale(RATE_SCALE, ROUND))
                .makingApplied(product.getMakingChargePerGram().setScale(RATE_SCALE, ROUND))
                .metalAmount(metalAmount)
                .makingAmount(makingAmount)
                .lineSubtotal(lineSubtotal)
                .taxAmount(taxAmount)
                .lineTotal(lineTotal)
                .build();
    }

    public InvoiceItem toInvoiceItem(Product product, LineCalculation calc, Long dailyRateId) {
        return InvoiceItem.builder()
                .product(product)
                .productName(product.getName())
                .weightGrams(calc.getWeightGrams())
                .rateApplied(calc.getRateApplied())
                .makingApplied(calc.getMakingApplied())
                .metalAmount(calc.getMetalAmount())
                .makingAmount(calc.getMakingAmount())
                .lineSubtotal(calc.getLineSubtotal())
                .taxAmount(calc.getTaxAmount())
                .lineTotal(calc.getLineTotal())
                .dailyRateId(dailyRateId)
                .build();
    }

    private BigDecimal applyWastage(BigDecimal weight, BigDecimal wastagePercent) {
        if (wastagePercent == null || wastagePercent.compareTo(BigDecimal.ZERO) <= 0) {
            return weight;
        }
        BigDecimal factor = BigDecimal.ONE.add(
                wastagePercent.divide(BigDecimal.valueOf(100), RATE_SCALE, ROUND));
        return weight.multiply(factor).setScale(RATE_SCALE, ROUND);
    }

    @Getter
    @Builder
    public static class LineCalculation {
        private BigDecimal weightGrams;
        private BigDecimal effectiveWeight;
        private BigDecimal rateApplied;
        private BigDecimal makingApplied;
        private BigDecimal metalAmount;
        private BigDecimal makingAmount;
        private BigDecimal lineSubtotal;
        private BigDecimal taxAmount;
        private BigDecimal lineTotal;
    }
}
