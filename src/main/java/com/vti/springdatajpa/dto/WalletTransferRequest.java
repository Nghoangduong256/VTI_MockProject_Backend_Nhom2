package com.vti.springdatajpa.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import java.io.Serializable;

@Data
public class WalletTransferRequest implements Serializable {
    @NotBlank(message = "Account number is required")
    private String toAccountNumber;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @DecimalMax(value = "5000000.00", message = "Maximum transfer amount is 5,000,000 USD per transaction")
    private Double amount;
    
    private String description; // Optional description for transfer
}
