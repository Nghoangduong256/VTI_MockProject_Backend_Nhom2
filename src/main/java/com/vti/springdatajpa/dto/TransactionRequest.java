package com.vti.springdatajpa.dto;

import lombok.Data;

@Data
public class TransactionRequest {
    private String type; // "topup", "transfer"
    private Double amount;
    private String note;
    // For transfer
    private String toUserName;
    // For topup
    private String sourceCardId;
}
