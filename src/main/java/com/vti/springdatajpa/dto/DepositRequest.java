package com.vti.springdatajpa.dto;

import lombok.Data;

@Data
public class DepositRequest {
    private Integer walletId;
    private Double amount;
}
