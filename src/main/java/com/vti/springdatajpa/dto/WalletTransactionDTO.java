package com.vti.springdatajpa.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WalletTransactionDTO {
    private Integer transactionId;
    private String transactionType; // TRANSFER_OUT, TRANSFER_IN, DEPOSIT, WITHDRAW
    private String direction; // OUT, IN
    private Double amount;
    private Double balanceBefore;
    private Double balanceAfter;
    private String partnerAccountNumber;
    private String partnerAccountName;
    private String description;
    private LocalDateTime timestamp;
    private String status; // COMPLETED, PENDING, FAILED
    private String referenceId;
}
