package com.vti.springdatajpa.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionDTO {
    private UUID id;
    private String type; // e.g. "Transfer", "Topup"
    private String description; // or title
    private String category;
    private Double amount;
    private LocalDateTime date;
    private String status;
    private String direction; // "IN", "OUT"
}
