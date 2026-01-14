package com.vti.springdatajpa.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminWalletDTO {
    private Integer id;
    private String accountNumber;
    private Double availableBalance;
    private LocalDateTime createdAt;
    private String status;
    private Integer userId;
}
