package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.WalletBalanceDTO;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.entity.Wallet;
import com.vti.springdatajpa.repository.UserRepository;
import com.vti.springdatajpa.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    public WalletBalanceDTO getBalance(String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElse(null);

        WalletBalanceDTO dto = new WalletBalanceDTO();
        if (wallet != null) {
            dto.setBalance(wallet.getBalance());
        } else {
            dto.setBalance(0.0);
        }
        dto.setMonthlyChangePercent(2.5);
        return dto;
    }
}
