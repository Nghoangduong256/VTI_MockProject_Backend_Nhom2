package com.vti.springdatajpa.dto;

import lombok.Data;

@Data
public class WalletBalanceDTO {
    private Double balance;
    private Double monthlyChangePercent; // For visualization
}
