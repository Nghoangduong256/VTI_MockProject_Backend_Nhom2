package com.vti.springdatajpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Session {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    private User user;

    private String deviceInfo;
    private String ipAddress;

    private String refreshTokenHash;
    private boolean revoked;

    private LocalDateTime createdAt;
    private LocalDateTime lastActiveAt;
}
