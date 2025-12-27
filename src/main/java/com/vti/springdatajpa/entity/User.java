package com.vti.springdatajpa.entity;
import com.vti.springdatajpa.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String userName;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;
    private String fullName;

    @Column(nullable = false)
    private String passwordHash;

    private String pinHash;

    private boolean isActive;
    private boolean isVerified;

    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "user")
    private Wallet wallet;

    private String avatarUrl;
    private String membership; // e.g., "Silver", "Gold", "Platinum"

}
