package com.vti.springdatajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private String type;
    private long expiresIn;
    private String userName;
    private String email;
    private String fullName;
    private java.util.List<String> roles;
    private String avatar;
    private String membership;

}
