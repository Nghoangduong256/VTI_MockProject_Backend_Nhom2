package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.SpendingAnalyticsDTO;
import com.vti.springdatajpa.dto.WalletSummaryDTO;
import com.vti.springdatajpa.entity.Transaction;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.entity.Wallet;
import com.vti.springdatajpa.entity.enums.TransactionDirection;
import com.vti.springdatajpa.entity.enums.TransactionStatus;
import com.vti.springdatajpa.repository.TransactionRepository;
import com.vti.springdatajpa.repository.UserRepository;
import com.vti.springdatajpa.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public WalletSummaryDTO getWalletSummary(String username, String period) {
        User user = findUserByUsernameOrEmail(username);
        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        LocalDate startDate;
        LocalDate endDate;
        String periodLabel;

        if ("current".equals(period)) {
            YearMonth currentMonth = YearMonth.now();
            startDate = currentMonth.atDay(1);
            endDate = currentMonth.atEndOfMonth();
            periodLabel = currentMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        } else {
            // Parse custom period like "2024-01"
            YearMonth targetMonth = YearMonth.parse(period);
            startDate = targetMonth.atDay(1);
            endDate = targetMonth.atEndOfMonth();
            periodLabel = period;
        }

        // Get transactions for the period
        List<Transaction> transactions = transactionRepository.findByWalletIdAndCreatedAtBetween(
                wallet.getId(), startDate.atStartOfDay(), endDate.atTime(23, 59, 59));

        // Calculate income and expense
        Double income = 0.0;
        Double expense = 0.0;

        for (Transaction transaction : transactions) {
            // Only include COMPLETED transactions in calculations
            if (transaction.getStatus() == TransactionStatus.COMPLETED) {
                if (transaction.getDirection() == TransactionDirection.IN) {
                    income += transaction.getAmount();
                } else {
                    expense += transaction.getAmount();
                }
            }
        }

        WalletSummaryDTO summary = new WalletSummaryDTO();
        summary.setIncome(income);
        summary.setExpense(expense);
        summary.setPeriod(periodLabel);

        return summary;
    }

    public List<SpendingAnalyticsDTO> getSpendingAnalytics(String username, String range) {
        User user = findUserByUsernameOrEmail(username);
        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        LocalDate endDate = LocalDate.now();
        LocalDate startDate;
        List<String> labels;

        switch (range) {
            case "7days":
                startDate = endDate.minusDays(6);
                labels = generateLast7DaysLabels();
                break;
            case "30days":
                startDate = endDate.minusDays(29);
                labels = generateLast30DaysLabels();
                break;
            case "90days":
                startDate = endDate.minusDays(89);
                labels = generateLast90DaysLabels();
                break;
            default:
                startDate = endDate.minusDays(6);
                labels = generateLast7DaysLabels();
                break;
        }

        // Get expense transactions for the range
        List<Transaction> transactions = transactionRepository.findByWalletIdAndCreatedAtBetween(
                wallet.getId(), startDate.atStartOfDay(), endDate.atTime(23, 59, 59));

        // Filter only expenses (OUT direction)
        List<Transaction> expenses = transactions.stream()
                .filter(t -> t.getDirection() == TransactionDirection.OUT)
                .collect(Collectors.toList());

        // Group by day and calculate totals
        List<SpendingAnalyticsDTO> analytics = new ArrayList<>();
        
        for (String label : labels) {
            Double dailySpending = expenses.stream()
                    .filter(t -> {
                        LocalDate transactionDate = t.getCreatedAt().toLocalDate();
                        String dayLabel = getDayLabel(transactionDate, range);
                        return label.equals(dayLabel);
                    })
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            SpendingAnalyticsDTO data = new SpendingAnalyticsDTO();
            data.setLabel(label);
            data.setValue(dailySpending);
            analytics.add(data);
        }

        return analytics;
    }

    private List<String> generateLast7DaysLabels() {
        List<String> labels = new ArrayList<>();
        LocalDate startDate = LocalDate.now().minusDays(6);
        
        for (int i = 0; i < 7; i++) {
            labels.add(startDate.plusDays(i).getDayOfWeek().toString().substring(0, 3));
        }
        
        return labels;
    }

    private List<String> generateLast30DaysLabels() {
        List<String> labels = new ArrayList<>();
        LocalDate startDate = LocalDate.now().minusDays(29);
        
        for (int i = 0; i < 30; i++) {
            labels.add(startDate.plusDays(i).toString().substring(5)); // MM-DD format
        }
        
        return labels;
    }

    private List<String> generateLast90DaysLabels() {
        List<String> labels = new ArrayList<>();
        
        for (int i = 0; i < 90; i += 7) { // Weekly aggregation
            labels.add("Week " + ((i / 7) + 1));
        }
        
        return labels;
    }

    private String getDayLabel(LocalDate transactionDate, String range) {
        switch (range) {
            case "7days":
                return transactionDate.getDayOfWeek().toString().substring(0, 3);
            case "30days":
                return transactionDate.toString().substring(5); // MM-DD
            case "90days":
                return "Week " + ((transactionDate.getDayOfYear() - 1) / 7 + 1);
            default:
                return transactionDate.getDayOfWeek().toString().substring(0, 3);
        }
    }

    /**
     * Flexible user lookup - tries username first, then email
     */
    private User findUserByUsernameOrEmail(String identity) {
        System.out.println("DashboardService - Looking for user with identity: " + identity);
        
        // Try username first
        User user = userRepository.findByUserName(identity).orElse(null);
        if (user != null) {
            System.out.println("DashboardService - Found user by username: " + identity);
            return user;
        }
        
        // Try email
        user = userRepository.findByEmail(identity).orElse(null);
        if (user != null) {
            System.out.println("DashboardService - Found user by email: " + identity);
            return user;
        }
        
        System.out.println("DashboardService - User not found with identity: " + identity);
        throw new RuntimeException("User not found with identity: " + identity);
    }
}
