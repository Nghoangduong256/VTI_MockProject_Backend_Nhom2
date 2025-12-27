package com.vti.springdatajpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "qr_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QRCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Wallet wallet;

    @Column(columnDefinition = "TEXT")
    private String codeValue;

    @Enumerated(EnumType.STRING)
    private QRType type;

    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}
