package com.vti.springdatajpa.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TransferHistoryDTO {
    private Integer id;
    private String partnerName;
    private String direction;  // IN, OUT
    private Double amount;
    private String status;
    private String note;
    private LocalDateTime createdAt;
    private String type;
    private String referenceId;
    private Boolean success;  // ✅ Thêm trường success
}

