package com.vti.springdatajpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "bank_transfers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    private Transaction transaction;

    @ManyToOne
    private BankAccount bankAccount;

    private String bankReference;
    private String provider;

    private LocalDateTime processedAt;

    @Enumerated(EnumType.STRING)
    private BankTransferStatus status;

}
