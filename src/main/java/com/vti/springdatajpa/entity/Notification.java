package com.vti.springdatajpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Notification {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    private User user;

    private String type;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private boolean isRead;
    private LocalDateTime createdAt;
}
