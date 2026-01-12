package com.vti.springdatajpa.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransferDetailDTO {
    private Integer id;
    private String direction;
    private Double amount;
    private String status;
    private String type;
    private String partnerName;
    private String note;
    private String referenceId;
    private LocalDateTime createdAt;
}
