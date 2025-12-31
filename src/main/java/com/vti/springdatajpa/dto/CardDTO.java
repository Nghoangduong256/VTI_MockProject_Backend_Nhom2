package com.vti.springdatajpa.dto;

import lombok.Data;

@Data
public class CardDTO {
    private Integer id;
    private String cardNumber; // Full for input, masked for output
    private String holderName;
    private String expiryDate; // MM/yy
    private String cvv; // Input only usually
    private String type; // DEBIT, CREDIT
    private String bankName;
    private String status;
    private String last4; // Helper for display
}
