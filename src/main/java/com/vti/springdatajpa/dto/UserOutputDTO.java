package com.vti.springdatajpa.dto;

import com.vti.springdatajpa.entity.Wallet;
import com.vti.springdatajpa.entity.enums.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserOutputDTO {
    private Integer id;
    private String username;
    private String email;
    private String phone;
    private String fullName;
    private boolean isActive;
    private boolean isVerified;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Wallet wallet;

}
