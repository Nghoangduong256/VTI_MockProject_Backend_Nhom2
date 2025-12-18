package com.vti.springdatajpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transfer_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferDetail {
    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    private Transaction transaction;

    private UUID counterpartyWalletId;
    private UUID counterpartyUserId;

    private String note;

    @Enumerated(EnumType.STRING)
    private TransferMethod method;

    private LocalDateTime createdAt;
}
