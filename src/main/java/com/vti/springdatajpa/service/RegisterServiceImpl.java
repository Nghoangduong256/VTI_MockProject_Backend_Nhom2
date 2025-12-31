package com.vti.springdatajpa.service;

import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.repository.RegisterRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RegisterServiceImpl implements RegisterService {

    private final RegisterRepository registerRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterServiceImpl(RegisterRepository registerRepository, PasswordEncoder passwordEncoder) {
        this.registerRepository = registerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createAccount(User user) {

        // check trùng username
        if (registerRepository.existsByUserName(user.getUserName())) {
            throw new RuntimeException("USERNAME_EXISTS");
        }
        // check trùng email
        if (registerRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("EMAIL_EXISTS");
        }
        // check trùng phone
        if (registerRepository.existsByPhone(user.getPhone())) {
            throw new RuntimeException("PHONE_EXISTS");
        }

        // mã hoá password
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        // tạo mã pin ngẫu nhiên 6 chữ số
        int pin = 100000 + new Random().nextInt(900000);
        user.setPinHash(String.valueOf(pin));
        // mặc định Active khi tạo
        user.setActive(true);
        // set Role mặc định USER
        user.setRole(com.vti.springdatajpa.entity.enums.Role.USER);
        // set createdAt
        user.setCreatedAt(java.time.LocalDateTime.now());
        return registerRepository.save(user);
    }
}
