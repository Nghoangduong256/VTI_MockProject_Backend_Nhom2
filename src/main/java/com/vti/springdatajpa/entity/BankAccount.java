package com.vti.springdatajpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vti.springdatajpa.entity.enums.BankAccountStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bank_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String code;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    private String bankCode;
    private String bankName;
    private String accountNumber;
    private String accountName;

    @Enumerated(EnumType.STRING)
    private BankAccountStatus status;

    private LocalDateTime createdAt;
}
