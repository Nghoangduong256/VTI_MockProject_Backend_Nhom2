package com.vti.springdatajpa.config;

import com.vti.springdatajpa.dto.SpendingActivityDTO;
import com.vti.springdatajpa.entity.*;
import com.vti.springdatajpa.entity.enums.*;
import com.vti.springdatajpa.repository.*;
import com.vti.springdatajpa.service.SpendingService;
import com.vti.springdatajpa.service.impl.SpendingServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;
    private final SpendingService spendingService; // ✅ THÊM DÒNG NÀY


    @Bean
    CommandLineRunner initUsers(
            UserRepository userRepository,
            WalletRepository walletRepository,
            BankAccountRepository bankAccountRepository,
            ContactRepository contactRepository,
            TransactionRepository transactionRepository,
            TransactionCategoryRepository categoryRepository
    ) {
        return args -> {

            // Create Transaction category
            TransactionCategory food = categoryRepository.findByName("Food")
                    .orElseGet(() -> categoryRepository.save(
                            new TransactionCategory(null, "Food", "restaurant", "#4CAF50")
                    ));

            TransactionCategory transport = categoryRepository.findByName("Transport")
                    .orElseGet(() -> categoryRepository.save(
                            new TransactionCategory(null, "Transport", "directions_car", "#2196F3")
                    ));

            TransactionCategory shopping = categoryRepository.findByName("Shopping")
                    .orElseGet(() -> categoryRepository.save(
                            new TransactionCategory(null, "Shopping", "shopping_bag", "#9C27B0")
                    ));

            TransactionCategory entertainment = categoryRepository.findByName("Entertainment")
                    .orElseGet(() -> categoryRepository.save(
                            new TransactionCategory(null, "Entertainment", "movie", "#FF9800")
                    ));

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
                wallet.setAccountNumber("12345");
                wallet.setBalance(1500000.0);
                wallet.setAvailableBalance(1500000.0);
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
                tx.setTransactionDate(LocalDateTime.now().minusDays(1));

                System.out.println("✅ Helper: Created normal user with wallet/card/contact");
            }

            // USER + WALLET (GIỮ NGUYÊN LOGIC CŨ)
            User user = userRepository.findByUserName("user").orElse(null);
            if (user == null) return;

            Wallet wallet = walletRepository.findByUser(user).orElse(null);
            if (wallet == null) return;

            // TRANSACTION SEED (SPENDING)
            if (transactionRepository.count() < 5) {
                LocalDateTime now = LocalDateTime.now();

                createSpendingTx(transactionRepository, wallet, food,
                        120_000, "Starbucks", now.minusDays(1));
                createSpendingTx(transactionRepository, wallet, transport,
                        80_000, "Grab", now.minusDays(2));
                createSpendingTx(transactionRepository, wallet, shopping,
                        350_000, "Shopee", now.minusDays(3));
                createSpendingTx(transactionRepository, wallet, entertainment,
                        150_000, "CGV Cinema", now.minusDays(4));
                createSpendingTx(transactionRepository, wallet, food,
                        90_000, "Bún bò", now.minusDays(5));

                System.out.println("✅ Helper: Seeded spending transactions");
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

            //Log test monthly spending
//            double monthlySpending = spendingService.getMonthlySpending();
//            System.out.println("🔥 Monthly Spending = " + monthlySpending);

            //Log test monthly spending activity
//            List<SpendingActivityDTO> activities =
//                    spendingService.getMonthlySpendingActivity();
//
//            System.out.println("🔥 Spending Activity:");
//            activities.forEach(a ->
//                    System.out.println(a.getDate() + " -> " + a.getTotalAmount())
//            );

            //Log test monthly spending breakdown
//            System.out.println("🔥 Spending Breakdown:");
//            spendingService.getMonthlySpendingBreakdown()
//                    .forEach(b ->
//                            System.out.println(
//                                    b.getCategoryName() + " -> " + b.getTotalAmount()
//                            )
//                    );

            // Log test recent spending transactions
//            System.out.println("🔥 Recent Transactions:");
//            spendingService.getRecentSpendingTransactions(5)
//                    .forEach(tx ->
//                            System.out.println(
//                                    tx.getTransactionDate()
//                                            + " | " + tx.getCategoryName()
//                                            + " | " + tx.getAmount()
//                            )
//                    );


        };
    }

    private void createSpendingTx(
            TransactionRepository repo,
            Wallet wallet,
            TransactionCategory category,
            double amount,
            String description,
            LocalDateTime txDate
    ) {
        Transaction tx = new Transaction();
        tx.setWallet(wallet);
        tx.setAmount(amount);
        tx.setFee(0.0);

        tx.setType(TransactionType.WITHDRAW);          // nghiệp vụ
        tx.setDirection(TransactionDirection.OUT);     // QUAN TRỌNG
        tx.setStatus(TransactionStatus.COMPLETED);

        tx.setCategory(category);
        tx.setMetadata(description);

        tx.setTransactionDate(txDate);                 // DÙNG CHO DASHBOARD
        tx.setCreatedAt(LocalDateTime.now());

        repo.save(tx);
    }

}
