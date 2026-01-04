package com.vti.springdatajpa.dto;

import lombok.Data;

@Data
public class QrResponseDTO {
    private String walletId;
    private String accountName;
    private String accountNumber;
    private String qrBase64;
}
