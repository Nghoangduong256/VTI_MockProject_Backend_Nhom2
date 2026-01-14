package com.vti.springdatajpa.config;

import com.vti.springdatajpa.entity.*;
import com.vti.springdatajpa.entity.enums.*;
import com.vti.springdatajpa.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initUsers(UserRepository userRepository,
            WalletRepository walletRepository,
            BankAccountRepository bankAccountRepository,
            ContactRepository contactRepository,
            TransactionRepository transactionRepository) {
        return args -> {
            // 1. ADMIN User
            // ============================================
            if (!userRepository.existsByUserName("admin")) {
                User admin = new User();
                admin.setUserName("admin");
                admin.setEmail("admin@vti.com");
                admin.setPasswordHash(passwordEncoder.encode("123456"));
                admin.setRole(Role.ADMIN);
                admin.setActive(true);
                admin.setVerified(true);
                admin.setCreatedAt(LocalDateTime.now());
                userRepository.save(admin);
                System.out.println("✅ Helper: Created admin user");
            }

            System.out.println("✅ User initialization check completed.");
        };
    }
}
