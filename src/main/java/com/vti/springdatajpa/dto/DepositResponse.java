package com.vti.springdatajpa.dto;

import lombok.Data;

@Data
public class DepositResponse {
    private String message;
    private Double newBalance;

    public DepositResponse(String depositSuccessful, Double balance) {
        this.message = depositSuccessful;
        this.newBalance = balance;
    }
}
