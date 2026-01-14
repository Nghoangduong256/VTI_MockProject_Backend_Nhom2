package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.AdminTransactionDTO;
import com.vti.springdatajpa.dto.AdminWalletDTO;
import com.vti.springdatajpa.entity.Transaction;
import com.vti.springdatajpa.entity.Wallet;
import com.vti.springdatajpa.entity.enums.WalletStatus;
import com.vti.springdatajpa.service.TransactionService;
import com.vti.springdatajpa.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final WalletRepository walletRepository;

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

    @GetMapping("/wallets")
    public ResponseEntity<List<AdminWalletDTO>> getAllWallets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        List<Wallet> wallets = walletRepository.findAll().stream()
                .skip(page * size)
                .limit(size)
                .collect(Collectors.toList());
        
        List<AdminWalletDTO> result = wallets.stream()
                .map(this::mapToAdminWalletDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(result);
    }

    @PutMapping("/wallets/lock/{id}")
    public ResponseEntity<String> lockWallet(@PathVariable Integer id) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        
        wallet.setStatus(WalletStatus.FROZEN);
        walletRepository.save(wallet);
        
        return ResponseEntity.ok("Wallet locked successfully");
    }

    @PutMapping("/wallets/unlock/{id}")
    public ResponseEntity<String> unlockWallet(@PathVariable Integer id) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        
        wallet.setStatus(WalletStatus.ACTIVE);
        walletRepository.save(wallet);
        
        return ResponseEntity.ok("Wallet unlocked successfully");
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

    private AdminWalletDTO mapToAdminWalletDTO(Wallet wallet) {
        AdminWalletDTO dto = new AdminWalletDTO();
        dto.setId(wallet.getId());
        dto.setAccountNumber(wallet.getAccountNumber());
        dto.setAvailableBalance(wallet.getAvailableBalance());
        dto.setCreatedAt(wallet.getCreatedAt());
        dto.setStatus(wallet.getStatus() != null ? wallet.getStatus().name() : null);
        dto.setUserId(wallet.getUser() != null ? wallet.getUser().getId() : null);
        return dto;
    }
}
