package com.vti.springdatajpa.config;

import com.vti.springdatajpa.entity.enums.Role;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.repository.UserRepository;
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
    CommandLineRunner initUsers(UserRepository userRepository) {
        return args -> {

            // ============================================
            // 1. ADMIN User
            // ============================================
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@vti.com");
                admin.setPasswordHash(passwordEncoder.encode("123456"));
                admin.setRole(Role.ADMIN);
                admin.setActive(true);
                admin.setVerified(true);
                admin.setCreatedAt(LocalDateTime.now());
                userRepository.save(admin);
                System.out.println("✅ Helper: Created admin user");
            }

            // ============================================
            // 2. USER - Normal User
            // ============================================
            if (!userRepository.existsByUsername("user")) {
                User normalUser = new User();
                normalUser.setUsername("user");
                normalUser.setEmail("user@vti.com");
                normalUser.setPasswordHash(passwordEncoder.encode("123456"));
                normalUser.setRole(Role.USER);
                normalUser.setActive(true);
                normalUser.setVerified(true);
                normalUser.setCreatedAt(LocalDateTime.now());
                userRepository.save(normalUser);
                System.out.println("✅ Helper: Created normal user");
            }

            // ============================================
            // 3. SUPPORT User
            // ============================================
            if (!userRepository.existsByUsername("support")) {
                User support = new User();
                support.setUsername("support");
                support.setEmail("support@vti.com");
                support.setPasswordHash(passwordEncoder.encode("123456"));
                support.setRole(Role.SUPPORT);
                support.setActive(true);
                support.setVerified(true);
                support.setCreatedAt(LocalDateTime.now());
                userRepository.save(support);
                System.out.println("✅ Helper: Created support user");
            }

            System.out.println("✅ User initialization check completed.");
        };
    }
}
