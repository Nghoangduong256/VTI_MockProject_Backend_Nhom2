package com.vti.springdatajpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vti.springdatajpa.entity.enums.WalletStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "wallets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String code;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    private String currency;

    private Double balance;
    private Double availableBalance;

    @Enumerated(EnumType.STRING)
    private WalletStatus status;

    private String accountNumber; // Số tài khoản = số điện thoại

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
