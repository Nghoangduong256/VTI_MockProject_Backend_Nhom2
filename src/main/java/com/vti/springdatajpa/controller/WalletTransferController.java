package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.AccountLookupResponse;
import com.vti.springdatajpa.dto.WalletTransactionDTO;
import com.vti.springdatajpa.dto.WalletTransferRequest;
import com.vti.springdatajpa.dto.WalletTransferResponse;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/wallet/transfers")
@RequiredArgsConstructor
public class WalletTransferController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<WalletTransferResponse> transferToWallet(@Valid @RequestBody WalletTransferRequest request) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName;
        if (principal instanceof com.vti.springdatajpa.entity.User) {
            userName = ((com.vti.springdatajpa.entity.User) principal).getUserName();
        } else if (principal instanceof String) {
            userName = (String) principal;
        } else {
            throw new RuntimeException("Unsupported identity type: " + principal.getClass().getName());
        }
        WalletTransferResponse response = transactionService.transferToWallet(userName, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lookup/{accountNumber}")
    public ResponseEntity<AccountLookupResponse> lookupAccount(@PathVariable String accountNumber) {
        AccountLookupResponse response = transactionService.lookupAccount(accountNumber);
        return ResponseEntity.ok(response);
    }
}
