package com.vti.springdatajpa.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserProfileUpdateDTO {
    private String firstName;
    private String lastName;
    private String phone;
    private LocalDate dateOfBirth;
    private String address;
}
