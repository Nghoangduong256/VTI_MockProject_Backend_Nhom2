package com.vti.springdatajpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "admin_actions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminAction {

    @Id
    @GeneratedValue
    private Integer id;

    private Integer adminId;

    private String actionType;
    private String targetType;
    private Integer targetId;

    private String reason;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    private LocalDateTime createdAt;
}

