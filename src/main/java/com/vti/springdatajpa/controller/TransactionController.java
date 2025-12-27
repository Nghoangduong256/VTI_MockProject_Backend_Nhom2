package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.TransactionDTO;
import com.vti.springdatajpa.dto.TransactionRequest;
import com.vti.springdatajpa.dto.TransferRequest;
import com.vti.springdatajpa.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<Page<TransactionDTO>> getTransactions(Pageable pageable) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(transactionService.getTransactions(username, pageable));
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> createTransfer(@RequestBody TransferRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        transactionService.createTransfer(username, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Void> createTransaction(@RequestBody TransactionRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        transactionService.createTransaction(username, request);
        return ResponseEntity.ok().build();
    }
}
