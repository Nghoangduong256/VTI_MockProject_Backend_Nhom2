package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.AvailableBalanceDTO;
import com.vti.springdatajpa.dto.WalletBalanceDTO;
import com.vti.springdatajpa.dto.WalletInfoDTO;
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

    public WalletBalanceDTO getBalance(Object identity) {
        User user = findUserByUsernameOrEmail(identity);

        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElse(null);

        WalletBalanceDTO dto = new WalletBalanceDTO();
        if (wallet != null) {
            dto.setBalance(wallet.getAvailableBalance());
        } else {
            dto.setBalance(0.0);
        }
        dto.setMonthlyChangePercent(2.5);
        return dto;
    }

    public WalletInfoDTO getWalletInfo(Object identity) {
        System.out.println("WalletService.getWalletInfo called with identity: " + identity);
        
        User user = findUserByUsernameOrEmail(identity);
        System.out.println("Found user: " + user);
        System.out.println("User ID: " + user.getId());
        System.out.println("User phone: " + user.getPhone());

        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        
        System.out.println("Found wallet: " + wallet);
        System.out.println("Wallet ID: " + wallet.getId());
        System.out.println("Wallet code: " + wallet.getCode());
        System.out.println("Wallet accountNumber: " + wallet.getAccountNumber());
        System.out.println("Wallet balance: " + wallet.getAvailableBalance());

        WalletInfoDTO dto = new WalletInfoDTO();
        dto.setWalletId(wallet.getCode() != null ? wallet.getCode() : "WALLET" + wallet.getId());
        dto.setAccountName(user.getFullName());
        dto.setAccountNumber(wallet.getAccountNumber() != null ? wallet.getAccountNumber() : user.getPhone());
        dto.setCurrency(wallet.getCurrency() != null ? wallet.getCurrency() : "VND");
        dto.setBalance(wallet.getAvailableBalance());

        System.out.println("Created DTO: " + dto);
        return dto;
    }

    public Wallet getWalletByCode(String walletCode) {
        return walletRepository.findByCode(walletCode)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }

    public AvailableBalanceDTO getAvailableBalance(Object identity) {
        User user = findUserByUsernameOrEmail(identity);
        
        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        
        AvailableBalanceDTO dto = new AvailableBalanceDTO();
        dto.setTotalBalance(wallet.getBalance());
        dto.setAvailableBalance(wallet.getAvailableBalance());
        dto.setHeldBalance(wallet.getBalance() - wallet.getAvailableBalance());
        dto.setCurrency(wallet.getCurrency() != null ? wallet.getCurrency() : "USD");
        
        return dto;
    }

    /**
     * Flexible user lookup - handles both string identity and User object
     * This fixes JWT identity mismatch issues
     */
    private User findUserByUsernameOrEmail(Object identity) {
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
        User user = userRepository.findByUserName(searchIdentity).orElse(null);
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

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() <= 4) {
            return "0x71C...9A23"; // Default masked format
        }
        
        String prefix = accountNumber.substring(0, 3);
        String suffix = accountNumber.substring(accountNumber.length() - 3);
        return "0x" + prefix + "..." + suffix;
    }
}
