package com.vti.springdatajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WalletSelectDTO {
    private Integer walletId;
    private String ownerName;
    private String walletCode;
    private String accountNumber;
}
