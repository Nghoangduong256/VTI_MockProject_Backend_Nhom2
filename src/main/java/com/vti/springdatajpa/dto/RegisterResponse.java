package com.vti.springdatajpa.dto;

import lombok.Data;

@Data
public class RegisterResponse {
    private String message;
    private Integer userId;
    private String accountNumber;
    private String walletId;
}
