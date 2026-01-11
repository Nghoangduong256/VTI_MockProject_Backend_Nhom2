package com.vti.springdatajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RecentTransactionDTO {
    private LocalDateTime transactionDate;
    private String categoryName;
    private String description;
    private Double amount;
    private String icon;
    private String color;
}
