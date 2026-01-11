package com.vti.springdatajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpendingSummaryResponse {
    private Double availableBalance;

    private Double monthlySpending;

    private List<SpendingActivityDTO> spendingActivity;

    private List<SpendingBreakdownDTO> spendingBreakdown;

    private List<RecentTransactionDTO> recentTransactions;

    private Double monthlySaving; // mock data
}
