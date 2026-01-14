package com.vti.springdatajpa.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class TransferRequest {
    @NotNull(message = "Recipient user ID is required")
    private Integer toUserId;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    private Double amount;
    
    @Size(max = 200, message = "Note must not exceed 200 characters")
    private String note;
}
