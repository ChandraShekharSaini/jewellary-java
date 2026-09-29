package com.jewelry.billing.config;

import com.jewelry.billing.entity.*;
import com.jewelry.billing.repository.DailyRateRepository;
import com.jewelry.billing.repository.ProductRepository;
import com.jewelry.billing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final DailyRateRepository dailyRateRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUsers();
        seedProducts();
        seedRates();
    }

    private void seedUsers() {
        if (userRepository.count() > 0) {
            return;
        }
        userRepository.save(User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .fullName("Shop Admin")
                .role(UserRole.ADMIN)
                .build());
        userRepository.save(User.builder()
                .username("staff")
                .password(passwordEncoder.encode("staff123"))
                .fullName("Sales Staff")
                .role(UserRole.STAFF)
                .build());
    }

    private void seedProducts() {
        if (productRepository.count() > 0) {
            return;
        }
        productRepository.save(Product.builder()
                .name("Gold Chain")
                .metalType(MetalType.GOLD)
                .purity("22K")
                .makingChargePerGram(new BigDecimal("450.0000"))
                .wastagePercent(new BigDecimal("8.00"))
                .build());
        productRepository.save(Product.builder()
                .name("Gold Ring")
                .metalType(MetalType.GOLD)
                .purity("22K")
                .makingChargePerGram(new BigDecimal("550.0000"))
                .wastagePercent(new BigDecimal("10.00"))
                .build());
        productRepository.save(Product.builder()
                .name("Silver Anklet")
                .metalType(MetalType.SILVER)
                .purity("925")
                .makingChargePerGram(new BigDecimal("80.0000"))
                .build());
    }

    private void seedRates() {
        if (dailyRateRepository.count() > 0) {
            return;
        }
        dailyRateRepository.save(DailyRate.builder()
                .metalType(MetalType.GOLD)
                .purity("22K")
                .ratePerGram(new BigDecimal("6850.0000"))
                .source("SEED")
                .build());
        dailyRateRepository.save(DailyRate.builder()
                .metalType(MetalType.GOLD)
                .purity("24K")
                .ratePerGram(new BigDecimal("7480.0000"))
                .source("SEED")
                .build());
        dailyRateRepository.save(DailyRate.builder()
                .metalType(MetalType.SILVER)
                .purity("925")
                .ratePerGram(new BigDecimal("92.5000"))
                .source("SEED")
                .build());
    }
}
