package com.vti.springdatajpa.dto;

import lombok.Data;

@Data
public class WalletInDeposit {
    private Integer id;
    private Double balance;
    private Double availableBalance;

}
