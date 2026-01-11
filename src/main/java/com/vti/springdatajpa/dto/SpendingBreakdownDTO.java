package com.vti.springdatajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SpendingBreakdownDTO {
    private String categoryName;
    private Double totalAmount;
    private String icon;
    private String color;
}
