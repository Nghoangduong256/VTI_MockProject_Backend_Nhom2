package com.vti.springdatajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WalletSelectDTO {

    private Integer walletId;
    private Integer userId;
    private String fullName;
    private String accountNumber;

}
