package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.RecentTransactionDTO;
import com.vti.springdatajpa.dto.SpendingActivityDTO;
import com.vti.springdatajpa.dto.SpendingBreakdownDTO;
import com.vti.springdatajpa.dto.SpendingSummaryResponse;

import java.util.List;

public interface SpendingService {
    public double getMonthlySpending();

    List<SpendingActivityDTO> getMonthlySpendingActivity();

    List<SpendingBreakdownDTO> getMonthlySpendingBreakdown();

    List<RecentTransactionDTO> getRecentSpendingTransactions(int limit);

    public SpendingSummaryResponse getSpendingSummary(Integer userId) ;

    public SpendingSummaryResponse getSpendingSummaryByUsername(String username);

}
