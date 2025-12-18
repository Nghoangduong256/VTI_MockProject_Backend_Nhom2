package com.vti.springdatajpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    private TransactionDirection direction;

    private Double amount;
    private Double fee;

    private Double balanceBefore;
    private Double balanceAfter;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private String referenceId;
    private String idempotencyKey;

    private UUID relatedTxId;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
