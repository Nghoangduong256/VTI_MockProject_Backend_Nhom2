package com.vti.springdatajpa.dto;

import java.time.LocalDateTime;

public record WithdrawResponse(
        Integer transactionId,
        String status,
        Double amount,
        Double fee,
        Double totalDebit,
        Double balanceBefore,
        Double availableBalanceBefore,
        Double availableBalanceAfter,
        LocalDateTime createdAt
) {}