package com.vti.springdatajpa.service.impl;

import com.vti.springdatajpa.dto.RecentTransactionDTO;
import com.vti.springdatajpa.dto.SpendingActivityDTO;
import com.vti.springdatajpa.dto.SpendingBreakdownDTO;
import com.vti.springdatajpa.dto.SpendingSummaryResponse;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.entity.Wallet;
import com.vti.springdatajpa.entity.enums.TransactionDirection;
import com.vti.springdatajpa.entity.enums.TransactionStatus;
import com.vti.springdatajpa.repository.TransactionRepository;
import com.vti.springdatajpa.repository.UserRepository;
import com.vti.springdatajpa.repository.WalletRepository;
import com.vti.springdatajpa.service.SpendingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpendingServiceImpl implements SpendingService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    @Override
    public SpendingSummaryResponse getSpendingSummaryByUsername(String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return getSpendingSummary(user.getId());
    }


    @Override
    public SpendingSummaryResponse getSpendingSummary(Integer userId) {

        // 1. Wallet → Total Balance
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        // 2. Monthly spending
        double monthlySpending = getMonthlySpending();

        // 3. Spending activity (bar chart)
        List<SpendingActivityDTO> spendingActivity =
                getMonthlySpendingActivity();

        // 4. Spending breakdown (pie chart)
        List<SpendingBreakdownDTO> spendingBreakdown =
                getMonthlySpendingBreakdown();

        // 5. Recent transactions
        List<RecentTransactionDTO> recentTransactions =
                getRecentSpendingTransactions(10);

        // 6. Monthly saving (mock tạm, sẽ refine sau)
        double monthlySaving = 1_100_000.0;

        return SpendingSummaryResponse.builder()
                .availableBalance(wallet.getAvailableBalance())
                .monthlySpending(monthlySpending)
                .spendingActivity(spendingActivity)
                .spendingBreakdown(spendingBreakdown)
                .recentTransactions(recentTransactions)
                .monthlySaving(monthlySaving)
                .build();
    }

    @Override
    public double getMonthlySpending() {

        LocalDate now = LocalDate.now();

        LocalDateTime startOfMonth = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = now
                .withDayOfMonth(now.lengthOfMonth())
                .atTime(23, 59, 59);

        Double total = transactionRepository
                .sumAmountByDirectionAndStatusAndDateBetween(
                        TransactionDirection.OUT,
                        TransactionStatus.COMPLETED,
                        startOfMonth,
                        endOfMonth
                );

        // amount của bạn đang là số dương → trả thẳng
        return total != null ? total : 0.0;
    }

    @Override
    public List<SpendingActivityDTO> getMonthlySpendingActivity() {
        LocalDate now = LocalDate.now();
        LocalDateTime start = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = now.withDayOfMonth(now.lengthOfMonth()).atTime(23, 59, 59);

        List<Object[]> rows =
                transactionRepository.findDailySpendingActivity(
                        TransactionDirection.OUT.name(),
                        TransactionStatus.COMPLETED.name(),
                        start,
                        end
                );

        return rows.stream()
                .map(r -> new SpendingActivityDTO(
                        ((java.sql.Date) r[0]).toLocalDate(),
                        ((Number) r[1]).doubleValue()
                ))
                .toList();
    }

    @Override
    public List<SpendingBreakdownDTO> getMonthlySpendingBreakdown() {
        LocalDate now = LocalDate.now();
        LocalDateTime start = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = now.withDayOfMonth(now.lengthOfMonth()).atTime(23, 59, 59);

        List<Object[]> rows =
                transactionRepository.findSpendingBreakdown(
                        TransactionDirection.OUT.name(),
                        TransactionStatus.COMPLETED.name(),
                        start,
                        end
                );

        return rows.stream()
                .map(r -> new SpendingBreakdownDTO(
                        (String) r[0],                    // category_name
                        ((Number) r[1]).doubleValue(),    // total_amount
                        (String) r[2],                    // icon
                        (String) r[3]                     // color
                ))
                .toList();
    }

    @Override
    public List<RecentTransactionDTO> getRecentSpendingTransactions(int limit) {
        List<Object[]> rows =
                transactionRepository.findRecentSpendingTransactions(
                        TransactionDirection.OUT.name(),
                        TransactionStatus.COMPLETED.name(),
                        limit
                );

        return rows.stream()
                .map(r -> new RecentTransactionDTO(
                        ((Timestamp) r[0]).toLocalDateTime(), // tx_date
                        (String) r[1],                        // category_name
                        (String) r[2],                        // description
                        ((Number) r[3]).doubleValue(),        // amount
                        (String) r[4],                        // icon
                        (String) r[5]                         // color
                ))
                .toList();
    }
}
