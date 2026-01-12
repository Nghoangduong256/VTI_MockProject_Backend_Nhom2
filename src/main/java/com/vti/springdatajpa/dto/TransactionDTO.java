package com.vti.springdatajpa.dto;

import com.vti.springdatajpa.entity.Transaction;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TransactionDTO {
    private Integer id;
    private String type; // e.g. "Transfer", "Topup"
    private String description; // or title
    private String category;
    private Double amount;
    private LocalDateTime date;
    private String status;
    private String direction; // "IN", "OUT"

    public static TransactionDTO fromEntity(Transaction tx) {
        TransactionDTO dto = new TransactionDTO();

        dto.setId(tx.getId());
        dto.setAmount(tx.getAmount());
        dto.setDate(tx.getCreatedAt());
        dto.setDirection(tx.getDirection().name());

        // type (tuỳ business – map an toàn)
        if (tx.getType() != null) {
            dto.setType(tx.getType().name());
        }

        // status
        if (tx.getStatus() != null) {
            dto.setStatus(tx.getStatus().name());
        }

        // category
        if (tx.getCategory() != null) {
            dto.setCategory(tx.getCategory().getName());
        }

        return dto;
    }

}
