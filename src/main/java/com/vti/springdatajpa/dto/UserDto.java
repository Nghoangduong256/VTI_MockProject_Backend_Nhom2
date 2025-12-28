package com.vti.springdatajpa.dto;

import com.vti.springdatajpa.entity.enums.Role;
import com.vti.springdatajpa.entity.Wallet;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class UserDto {

    private String firstName;

    private String lastName;

    private String avatar;

    private String username;

    private String email;

    private String phone;

    private LocalDate dateOfBirth;

    private String address;
}
