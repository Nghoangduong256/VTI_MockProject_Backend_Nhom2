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

            // ============================================
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

            // ============================================
            // 2. USER - Normal User
            // ============================================
            if (!userRepository.existsByUserName("user")) {
                User normalUser = new User();
                normalUser.setUserName("user");
                normalUser.setEmail("user@vti.com");
                normalUser.setPasswordHash(passwordEncoder.encode("123456"));
                normalUser.setRole(Role.USER);
                normalUser.setFullName("Nguyen Van User");
                normalUser.setAvatarUrl("https://i.pravatar.cc/150?u=user");
                normalUser.setMembership("Gold");
                normalUser.setActive(true);
                normalUser.setVerified(true);
                normalUser.setCreatedAt(LocalDateTime.now());
                User savedUser = userRepository.save(normalUser);

                // Create Wallet
                Wallet wallet = new Wallet();
                wallet.setUser(savedUser);
                wallet.setBalance(1500000.0);
                wallet.setCurrency("VND");
                wallet.setStatus(WalletStatus.ACTIVE);
                wallet.setCreatedAt(LocalDateTime.now());
                walletRepository.save(wallet);

                // Create BankAccount (Card)
                BankAccount bankAccount = new BankAccount();
                bankAccount.setUser(savedUser);
                bankAccount.setBankName("TPBank");
                bankAccount.setAccountNumber("9876543210");
                bankAccount.setAccountName("NGUYEN VAN USER");
                bankAccount.setStatus(BankAccountStatus.ACTIVE);
                bankAccount.setCreatedAt(LocalDateTime.now());
                bankAccountRepository.save(bankAccount);

                // Create Contact
                Contact contact = new Contact();
                contact.setUser(savedUser);
                contact.setName("Friend One");
                contact.setAvatarUrl("https://i.pravatar.cc/150?u=friend1");
                contact.setAccountNumber("111222333");
                contactRepository.save(contact);

                // Create Transaction
                Transaction tx = new Transaction();
                tx.setWallet(wallet);
                tx.setAmount(500000.0);
                tx.setType(TransactionType.DEPOSIT);
                tx.setStatus(TransactionStatus.COMPLETED);
                tx.setCreatedAt(LocalDateTime.now().minusDays(1));
                transactionRepository.save(tx);

                System.out.println("✅ Helper: Created normal user with wallet/card/contact");
            }

            // ============================================
            // 3. SUPPORT User
            // ============================================
            if (!userRepository.existsByUserName("support")) {
                User support = new User();
                support.setUserName("support");
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
