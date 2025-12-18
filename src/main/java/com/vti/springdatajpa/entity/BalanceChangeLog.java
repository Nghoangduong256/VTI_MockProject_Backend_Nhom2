package com.vti.springdatajpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "balance_change_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BalanceChangeLog {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    private Wallet wallet;

    @ManyToOne
    private Transaction transaction;

    private Double delta;
    private Double balanceBefore;
    private Double balanceAfter;

    private LocalDateTime createdAt;
}
