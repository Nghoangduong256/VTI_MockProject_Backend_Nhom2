package com.vti.springdatajpa.dto;

import lombok.Data;

@Data
public class UserProfileDTO {
    private String fullName;
    private String email;
    private String userName;
    private String avatarUrl;
    private String membership;
}
