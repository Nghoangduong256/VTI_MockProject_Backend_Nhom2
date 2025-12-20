package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.form.RegisterForm;
import com.vti.springdatajpa.service.RegisterService;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value ="api/E-Wallet/register")
public class RegisterController {
    private final RegisterService registerService;
    private final ModelMapper modelMapper;

    public RegisterController(RegisterService registerService, ModelMapper modelMapper) {
        this.registerService = registerService;
        this.modelMapper = modelMapper;
    }
    // Tạo tài khoản mới
    @PostMapping
    public void createAccount(@RequestBody RegisterForm registerForm){
        User user = modelMapper.map(registerForm,User.class);
        registerService.createAccount(user);
    }
    //



}
