package com.vti.springdatajpa.service;

import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.entity.Wallet;
import com.vti.springdatajpa.entity.enums.WalletStatus;
import com.vti.springdatajpa.repository.RegisterRepository;
import com.vti.springdatajpa.repository.WalletRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RegisterServiceImpl implements RegisterService {

    private final RegisterRepository registerRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterServiceImpl(RegisterRepository registerRepository,
                               WalletRepository walletRepository,
                               PasswordEncoder passwordEncoder) {
        this.registerRepository = registerRepository;
        this.walletRepository = walletRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createAccount(User user) {

        // check trùng username/email/phone
        if (registerRepository.existsByUserName(user.getUserName())) {
            throw new RuntimeException("USERNAME_EXISTS");
        }
        if (registerRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("EMAIL_EXISTS");
        }
        if (registerRepository.existsByPhone(user.getPhone())) {
            throw new RuntimeException("PHONE_EXISTS");
        }

        // mã hoá password
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        int pin = 100000 + new Random().nextInt(900000);
        user.setPinHash(String.valueOf(pin));
        user.setActive(true);
        user.setRole(com.vti.springdatajpa.entity.enums.Role.USER);
        user.setCreatedAt(java.time.LocalDateTime.now());

        // Lưu user
        User savedUser = registerRepository.save(user);

        // Tạo wallet
        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);
        wallet.setCode("WALLET" + savedUser.getId());
        wallet.setCurrency("VND");
        wallet.setBalance(0.0);
        wallet.setAvailableBalance(0.0);
        wallet.setStatus(WalletStatus.ACTIVE);
        wallet.setAccountNumber(user.getPhone());
        wallet.setCreatedAt(java.time.LocalDateTime.now());

        walletRepository.save(wallet);

        return savedUser;
    }
}
