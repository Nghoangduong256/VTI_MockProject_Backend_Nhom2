package com.vti.springdatajpa.form;

import com.vti.springdatajpa.entity.Role;
import com.vti.springdatajpa.entity.Wallet;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class RegisterForm {
    private String userName;
    private String email;
    private String phone;
    private String fullName;
    private String passwordHash;

}
