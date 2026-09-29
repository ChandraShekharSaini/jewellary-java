package com.jewelry.billing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "metal_type", nullable = false, length = 20)
    private MetalType metalType;

    @Column(nullable = false, length = 10)
    private String purity;

    @Column(name = "making_charge_per_gram", nullable = false, precision = 10, scale = 4)
    private BigDecimal makingChargePerGram;

    @Column(name = "wastage_percent", precision = 5, scale = 2)
    private BigDecimal wastagePercent;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
