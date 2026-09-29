package com.jewelry.billing.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "daily_rates", indexes = {
        @Index(name = "idx_daily_rates_metal_purity", columnList = "metal_type, purity, updated_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "metal_type", nullable = false, length = 20)
    private MetalType metalType;

    @Column(nullable = false, length = 10)
    private String purity;

    @Column(name = "rate_per_gram", nullable = false, precision = 12, scale = 4)
    private BigDecimal ratePerGram;

    @CreationTimestamp
    @Column(name = "updated_at", nullable = false, updatable = false)
    private Instant updatedAt;

    @Column(name = "source", length = 50)
    private String source;

    @Column(name = "created_by")
    private Long createdBy;
}
