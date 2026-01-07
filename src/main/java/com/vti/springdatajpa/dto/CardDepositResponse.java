package com.vti.springdatajpa.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CardDepositResponse {
    private Integer transactionId;
    private Integer cardId;
    private String cardNumber; // Masked
    private Double amount;
    private Double previousCardBalance;
    private Double newCardBalance;
    private Double previousWalletBalance;
    private Double newWalletBalance;
    private String description;
    private LocalDateTime timestamp;
    private String status; // SUCCESS, FAILED
    private String message;
}
