package com.vti.springdatajpa.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminTransactionDTO {
    private String transactionId;
    private String walletId;
    private Double amount;
    private String status;
    private String type;
    private LocalDateTime createdAt;
}
