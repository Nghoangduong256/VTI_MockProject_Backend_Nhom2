package com.vti.springdatajpa.dto;

import lombok.Data;

@Data
public class SpendingAnalyticsDTO {
    private String label; // "Mon", "Tue", etc.
    private Double value; // Spending amount
}
