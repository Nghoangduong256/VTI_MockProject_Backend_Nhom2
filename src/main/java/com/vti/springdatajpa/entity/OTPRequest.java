package com.vti.springdatajpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "otp_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OTPRequest {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    private User user;

    private String purpose;
    private String otpCode;

    private boolean isUsed;

    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}
