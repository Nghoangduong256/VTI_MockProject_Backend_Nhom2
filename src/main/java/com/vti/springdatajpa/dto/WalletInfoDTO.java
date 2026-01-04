package com.vti.springdatajpa.dto;

import lombok.Data;

@Data
public class WalletInfoDTO {
    private String walletId;
    private String accountName;
    private String accountNumber;
    private String currency;
    private Double balance;
}
