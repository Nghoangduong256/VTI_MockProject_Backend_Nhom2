package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.WalletBalanceDTO;
import com.vti.springdatajpa.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @GetMapping("/balance")
    public ResponseEntity<WalletBalanceDTO> getBalance() {
        Object identity = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("Wallet Balance - JWT identity: " + identity);
        System.out.println("Identity type: " + identity.getClass().getName());
        return ResponseEntity.ok(walletService.getBalance(identity));
    }
}
