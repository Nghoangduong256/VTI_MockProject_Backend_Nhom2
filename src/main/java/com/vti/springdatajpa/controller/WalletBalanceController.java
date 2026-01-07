package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.AvailableBalanceDTO;
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
public class WalletBalanceController {

    private final WalletService walletService;

    @GetMapping("/available-balance")
    public ResponseEntity<AvailableBalanceDTO> getAvailableBalance() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("Available Balance - JWT identity: " + principal);
        
        AvailableBalanceDTO availableBalance = walletService.getAvailableBalance(principal);
        return ResponseEntity.ok(availableBalance);
    }
}
