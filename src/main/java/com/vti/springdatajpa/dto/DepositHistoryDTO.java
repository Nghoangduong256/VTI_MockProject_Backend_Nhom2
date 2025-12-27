package com.vti.springdatajpa.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vti.springdatajpa.entity.TransactionStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class DepositHistoryDTO {
    private Integer id;
    private Double amount;
    private String referenceId;
    private TransactionStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
