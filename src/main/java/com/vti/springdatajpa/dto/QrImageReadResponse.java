package com.vti.springdatajpa.dto;

import lombok.Data;

@Data
public class QrImageReadResponse {
    private String walletId;
    private String receiverName;
    private String accountNumber;
    private Double amount;
    private String currency;
    private Boolean valid;
    private Boolean transferReady;
}
