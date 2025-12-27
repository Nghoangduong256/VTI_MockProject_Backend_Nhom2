package com.vti.springdatajpa.dto;

import lombok.Data;

@Data
public class TransferRequest {
    private Integer toUserId; // or username
    private Double amount;
    private String note;
}
