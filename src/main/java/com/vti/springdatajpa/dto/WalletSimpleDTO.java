package com.vti.springdatajpa.dto;

import lombok.Data;

@Data
public class WalletSimpleDTO {
    private Integer walletId;
    private Double balance;

    public WalletSimpleDTO(Integer id, Double balance) {
        this.walletId = id;
        this.balance = balance;
    }
}
