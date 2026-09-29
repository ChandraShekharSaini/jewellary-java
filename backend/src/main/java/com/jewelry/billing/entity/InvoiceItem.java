package com.jewelry.billing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "product_name", nullable = false, length = 150)
    private String productName;

    @Column(name = "weight_grams", nullable = false, precision = 10, scale = 4)
    private BigDecimal weightGrams;

    @Column(name = "rate_applied", nullable = false, precision = 12, scale = 4)
    private BigDecimal rateApplied;

    @Column(name = "making_applied", nullable = false, precision = 12, scale = 4)
    private BigDecimal makingApplied;

    @Column(name = "metal_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal metalAmount;

    @Column(name = "making_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal makingAmount;

    @Column(name = "line_subtotal", nullable = false, precision = 14, scale = 2)
    private BigDecimal lineSubtotal;

    @Column(name = "tax_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "line_total", nullable = false, precision = 14, scale = 2)
    private BigDecimal lineTotal;

    @Column(name = "daily_rate_id")
    private Long dailyRateId;
}
