package com.vti.springdatajpa.form;

import com.vti.springdatajpa.entity.Role;
import com.vti.springdatajpa.entity.Wallet;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Data
public class RegisterForm {
    private String username;
    private String email;
    private String phone;
    private String fullName;
    private String passwordHash;

}
