package com.vti.springdatajpa.dto;

import lombok.Data;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Data
public class CardWithdrawRequest {
    @NotNull(message = "Card ID is required")
    private Integer cardId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @DecimalMax(value = "5000000.00", message = "Maximum withdraw amount is 5,000,000 USD per transaction")
    private Double amount;
    
    private String description; // Optional description for withdraw
}
