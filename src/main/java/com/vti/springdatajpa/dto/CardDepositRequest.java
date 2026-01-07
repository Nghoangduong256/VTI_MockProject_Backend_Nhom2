package com.vti.springdatajpa.dto;

import lombok.Data;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Data
public class CardDepositRequest {
    @NotNull(message = "Card ID is required")
    private Integer cardId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1000.0", message = "Minimum deposit amount is 1000 VND")
    @DecimalMax(value = "5000000.0", message = "Maximum deposit amount is 5,000,000 VND per transaction")
    private Double amount;
    
    private String description; // Optional description for deposit
}
