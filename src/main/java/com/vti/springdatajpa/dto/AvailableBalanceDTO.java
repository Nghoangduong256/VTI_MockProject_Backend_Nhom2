package com.vti.springdatajpa.dto;

import lombok.Data;

@Data
public class AvailableBalanceDTO {
    private Double totalBalance;        // Tổng số dư (balance)
    private Double availableBalance;    // Số dư khả dụng (availableBalance)
    private Double heldBalance;         // Số tiền đang giữ (hold)
    private String currency;           // Đơn vị tiền tệ
}
