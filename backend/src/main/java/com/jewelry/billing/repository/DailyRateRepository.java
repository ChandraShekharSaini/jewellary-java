package com.jewelry.billing.repository;

import com.jewelry.billing.entity.DailyRate;
import com.jewelry.billing.entity.MetalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DailyRateRepository extends JpaRepository<DailyRate, Long> {

    Optional<DailyRate> findFirstByMetalTypeAndPurityOrderByUpdatedAtDesc(
            MetalType metalType, String purity);

    List<DailyRate> findByMetalTypeOrderByUpdatedAtDesc(MetalType metalType);

    @Query(value = """
            SELECT r.* FROM daily_rates r
            INNER JOIN (
                SELECT metal_type, purity, MAX(updated_at) AS max_updated
                FROM daily_rates GROUP BY metal_type, purity
            ) latest ON r.metal_type = latest.metal_type
                AND r.purity = latest.purity AND r.updated_at = latest.max_updated
            ORDER BY r.metal_type, r.purity
            """, nativeQuery = true)
    List<DailyRate> findAllLatestRates();
}
