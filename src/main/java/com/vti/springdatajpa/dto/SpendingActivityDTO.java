package com.vti.springdatajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class SpendingActivityDTO {
    private LocalDate date;
    private Double totalAmount;

}
