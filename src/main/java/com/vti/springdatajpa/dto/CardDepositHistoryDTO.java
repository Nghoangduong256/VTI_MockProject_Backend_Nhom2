package com.vti.springdatajpa.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CardDepositHistoryDTO {
    private Integer transactionId;
    private Integer cardId;
    private String cardNumber; // Masked (****1234)
    private String bankName;
    private Double amount;
    private String description;
    private LocalDateTime timestamp;
    private String status; // SUCCESS, FAILED, PENDING
}
