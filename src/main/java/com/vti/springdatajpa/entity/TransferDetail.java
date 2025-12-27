package com.vti.springdatajpa.entity;

import com.vti.springdatajpa.entity.enums.TransferMethod;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToOne
    private Transaction transaction;

    private UUID counterpartyWalletId;
    private UUID counterpartyUserId;

    private String note;

    @Enumerated(EnumType.STRING)
    private TransferMethod method;

    private LocalDateTime createdAt;
}
