package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.DepositHistoryDTO;
import com.vti.springdatajpa.dto.DepositRequest;
import com.vti.springdatajpa.entity.*;
import com.vti.springdatajpa.entity.enums.TransactionDirection;
import com.vti.springdatajpa.entity.enums.TransactionStatus;
import com.vti.springdatajpa.entity.enums.TransactionType;
import com.vti.springdatajpa.repository.TransactionRepository;
import com.vti.springdatajpa.repository.WalletRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DepositServiceImpl implements DepositService {
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;

    public DepositServiceImpl(WalletRepository walletRepository, TransactionRepository transactionRepository, ModelMapper modelMapper) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Wallet deposit(DepositRequest request) {
        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        double balanceBefore = wallet.getBalance();
        double balanceAfter = balanceBefore + request.getAmount();

        // Update wallet
        wallet.setBalance(balanceAfter);
        wallet.setAvailableBalance(balanceAfter);
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        // Create transaction
        Transaction tx = new Transaction();
        tx.setWallet(wallet);
        tx.setType(TransactionType.DEPOSIT);
        tx.setDirection(TransactionDirection.IN);
        tx.setAmount(request.getAmount());
        tx.setFee(0.0);
        tx.setBalanceBefore(balanceBefore);
        tx.setBalanceAfter(balanceAfter);
        tx.setStatus(TransactionStatus.COMPLETED);
        tx.setReferenceId("DEMO_DEPOSIT_" + System.currentTimeMillis());
        tx.setCreatedAt(LocalDateTime.now());
        tx.setUpdatedAt(LocalDateTime.now());

        transactionRepository.save(tx);

        return wallet;
    }

    @Override
    public Wallet getWalletById(Integer id) {
        return walletRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }

    @Override
    public List<DepositHistoryDTO> getRecentDeposits(Integer walletId) {

        List<Transaction> transactions =
                transactionRepository.findTop5ByWallet_IdAndTypeOrderByCreatedAtDesc(
                        walletId,
                        TransactionType.DEPOSIT
                );

        return transactions.stream()
                .map(tx -> {
                    DepositHistoryDTO dto = new DepositHistoryDTO();
                    dto.setId(tx.getId());
                    dto.setAmount(tx.getAmount());
                    dto.setReferenceId(tx.getReferenceId());
                    dto.setStatus(tx.getStatus());       // enum OK
                    dto.setCreatedAt(tx.getCreatedAt()); // QUAN TRỌNG
                    return dto;
                })
                .toList();
    }

    @Override
    public Wallet getWalletByUserName(String userName) {
        return walletRepository.findByUser_UserName(userName)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user: " + userName));

    }

//    @Override
//    public Wallet getWalletByUserId(Integer userId) {
//        return walletRepository.findByUserId(userId)
//                .orElseThrow(() -> new RuntimeException("Wallet not found for user id: " + userId));
//    }

}
