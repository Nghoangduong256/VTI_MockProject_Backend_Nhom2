package com.vti.springdatajpa.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserProfileDTO {

    // Identity
    private String userName;
    private String email;

    // Name (phục vụ UI First / Last Name)
    private String firstName;
    private String lastName;

    // Avatar
    private String avatar;      // Base64 (MEDIUMTEXT)
    private String avatarUrl;   // fallback nếu cần

    // Status
    private boolean verified;
    private String membership;

    // Contact & personal info
    private String phone;
    private LocalDate dateOfBirth;
    private String address;
}
