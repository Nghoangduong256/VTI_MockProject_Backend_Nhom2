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
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        WalletTransferResponse response = transactionService.transferToWallet(user.getUserName(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<WalletTransactionDTO>> getRecentTransactions(
            @RequestParam(defaultValue = "10") int limit) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<WalletTransactionDTO> transactions = transactionService.getRecentTransactions(user.getUserName(), limit);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/lookup/{accountNumber}")
    public ResponseEntity<AccountLookupResponse> lookupAccount(@PathVariable String accountNumber) {
        AccountLookupResponse response = transactionService.lookupAccount(accountNumber);
        return ResponseEntity.ok(response);
    }
}
