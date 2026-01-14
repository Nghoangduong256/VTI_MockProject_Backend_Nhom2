package com.vti.springdatajpa.dto;

import com.vti.springdatajpa.entity.Wallet;
import com.vti.springdatajpa.entity.enums.Role;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserManagerDTO {

    private Integer id;

    private String userName;

    private String email;

    private String phone;

    private String fullName;

    private boolean isActive;

    private LocalDateTime createdAt;

}
