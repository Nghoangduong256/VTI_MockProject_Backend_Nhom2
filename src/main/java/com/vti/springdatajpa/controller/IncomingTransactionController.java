package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.IncomingTransactionDTO;
import com.vti.springdatajpa.entity.Transaction;
import com.vti.springdatajpa.entity.enums.TransactionDirection;
import com.vti.springdatajpa.repository.TransactionRepository;
import com.vti.springdatajpa.repository.UserRepository;
import com.vti.springdatajpa.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class IncomingTransactionController {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    @GetMapping("/incoming")
    public List<IncomingTransactionDTO> getIncomingTransactions(
            @RequestParam(defaultValue = "5") int limit) {
        
        Object identity = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("Incoming transactions - JWT identity: " + identity);
        System.out.println("Identity type: " + identity.getClass().getName());
        
        // Get user with flexible lookup
        var user = findUserByUsernameOrEmail(identity);
        
        var wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        
        // Get incoming transactions
        List<Transaction> transactions = transactionRepository
                .findIncomingTransactions(wallet.getId(), TransactionDirection.IN);
        
        // Apply limit
        if (limit > 0 && transactions.size() > limit) {
            transactions = transactions.subList(0, limit);
        }
        
        // Convert to DTO
        return transactions.stream()
                .map(this::mapToIncomingTransactionDTO)
                .collect(Collectors.toList());
    }

    private IncomingTransactionDTO mapToIncomingTransactionDTO(Transaction transaction) {
        IncomingTransactionDTO dto = new IncomingTransactionDTO();
        dto.setId(transaction.getId());
        dto.setType(transaction.getType().name());
        dto.setAmount(transaction.getAmount());
        dto.setDate(transaction.getCreatedAt());
        dto.setStatus(transaction.getStatus().name());
        
        // Set description based on transaction type
        switch (transaction.getType()) {
            case TRANSFER_IN:
                dto.setDescription("Chuyển tiền vào");
                break;
            case DEPOSIT:
                dto.setDescription("Nạp tiền vào ví");
                break;
            default:
                dto.setDescription("Giao dịch vào");
                break;
        }
        
        return dto;
    }

    /**
     * Flexible user lookup - handles both string identity and User object
     * This fixes JWT identity mismatch issues
     */
    private com.vti.springdatajpa.entity.User findUserByUsernameOrEmail(Object identity) {
        System.out.println("Looking for user with identity: " + identity);
        System.out.println("Identity type: " + identity.getClass().getName());
        
        String searchIdentity;
        
        // Handle User object case
        if (identity instanceof com.vti.springdatajpa.entity.User) {
            com.vti.springdatajpa.entity.User userObj = (com.vti.springdatajpa.entity.User) identity;
            searchIdentity = userObj.getUserName(); // Use username from User object
            System.out.println("Extracted username from User object: " + searchIdentity);
        } 
        // Handle string case (username or email)
        else if (identity instanceof String) {
            searchIdentity = (String) identity;
            System.out.println("Using string identity: " + searchIdentity);
        } 
        else {
            throw new RuntimeException("Unsupported identity type: " + identity.getClass().getName());
        }
        
        // Try username first
        var user = userRepository.findByUserName(searchIdentity).orElse(null);
        if (user != null) {
            System.out.println("Found user by username: " + searchIdentity);
            return user;
        }
        
        // Try email
        user = userRepository.findByEmail(searchIdentity).orElse(null);
        if (user != null) {
            System.out.println("Found user by email: " + searchIdentity);
            return user;
        }
        
        System.out.println("User not found with identity: " + searchIdentity);
        throw new RuntimeException("User not found with identity: " + searchIdentity);
    }
}
