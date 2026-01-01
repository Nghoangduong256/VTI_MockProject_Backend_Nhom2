package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.LoginRequest;
import com.vti.springdatajpa.dto.LoginResponse;
import com.vti.springdatajpa.dto.RegisterResponse;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.form.RegisterForm;
import com.vti.springdatajpa.service.AuthService;
import com.vti.springdatajpa.service.RegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RegisterService registerService;
    private final ModelMapper modelMapper;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterForm registerForm) {
        User user = modelMapper.map(registerForm, User.class);
        User savedUser = registerService.createAccount(user);
        
        RegisterResponse response = new RegisterResponse();
        response.setMessage("User registered successfully");
        response.setUserId(savedUser.getId());
        response.setAccountNumber(user.getPhone()); // Số tài khoản = số điện thoại
        response.setWalletId("WALLET" + savedUser.getId());
        
        return ResponseEntity.ok(response);
    }
}
