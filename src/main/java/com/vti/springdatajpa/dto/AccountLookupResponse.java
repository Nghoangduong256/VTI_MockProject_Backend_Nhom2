package com.vti.springdatajpa.dto;

import lombok.Data;

@Data
public class AccountLookupResponse {
    private String accountNumber;
    private String accountName;
    private String accountHolderName; // Add this field
    private String avatarUrl; // Add this field
    private String accountType; // WALLET
    private Boolean active;
    private String message;
    private Boolean found;
}
