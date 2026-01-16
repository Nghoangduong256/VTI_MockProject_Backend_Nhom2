package com.vti.springdatajpa.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WalletTransferResponse {
    private Integer transactionId;
    private String fromAccountNumber;
    private String toAccountNumber;
    private String toAccountName;
    private Double amount;
    private Double previousBalance;
    private Double newBalance;
    private String description;
    private LocalDateTime timestamp;
    private String status; // SUCCESS, FAILED
    private String message;
    private String transactionType; // WALLET_TRANSFER
    private Boolean success; // Add this field
}
