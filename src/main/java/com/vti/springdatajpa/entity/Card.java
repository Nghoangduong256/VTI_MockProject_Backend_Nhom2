package com.vti.springdatajpa.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String cardNumber;

    private String cardHolderName;

    private String expiryDate; // Format MM/yy

    private String cvv;

    private String bankName;

    private String type; // CREDIT, DEBIT

    @Enumerated(EnumType.STRING)
    private CardStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
