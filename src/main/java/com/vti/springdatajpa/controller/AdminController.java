package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.AdminTransactionDTO;
import com.vti.springdatajpa.entity.Transaction;
import com.vti.springdatajpa.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final TransactionService transactionService;

    @GetMapping("/transactions")
    public ResponseEntity<List<AdminTransactionDTO>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        List<Transaction> transactions = transactionService.getAllTransactionsForAdmin(page, size);
        
        List<AdminTransactionDTO> result = transactions.stream()
                .map(this::mapToAdminDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(result);
    }

    private AdminTransactionDTO mapToAdminDTO(Transaction transaction) {
        AdminTransactionDTO dto = new AdminTransactionDTO();
        dto.setTransactionId("TXN-" + transaction.getId());
        dto.setWalletId(transaction.getWallet() != null ? "WAL-" + transaction.getWallet().getId() : null);
        dto.setAmount(transaction.getAmount());
        dto.setStatus(transaction.getStatus() != null ? transaction.getStatus().name() : null);
        dto.setType(transaction.getType() != null ? transaction.getType().name() : null);
        dto.setCreatedAt(transaction.getCreatedAt());
        return dto;
    }
}
