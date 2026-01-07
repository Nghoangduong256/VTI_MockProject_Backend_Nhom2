package com.vti.springdatajpa.dto;

import lombok.Data;

@Data
public class WalletSummaryDTO {
    private Double income;
    private Double expense;
    private String period; // Format: "2024-01"
}
