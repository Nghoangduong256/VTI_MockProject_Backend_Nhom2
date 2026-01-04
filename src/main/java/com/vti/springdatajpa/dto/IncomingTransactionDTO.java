package com.vti.springdatajpa.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IncomingTransactionDTO {
    private Integer id;
    private String type;
    private Double amount;
    private LocalDateTime date;
    private String status;
    private String description;
}
