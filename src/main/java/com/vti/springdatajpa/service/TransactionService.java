package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.*;
import com.vti.springdatajpa.entity.*;
import com.vti.springdatajpa.entity.enums.TransactionDirection;
import com.vti.springdatajpa.entity.enums.TransactionStatus;
import com.vti.springdatajpa.entity.enums.TransactionType;
import com.vti.springdatajpa.repository.TransactionRepository;
import com.vti.springdatajpa.repository.UserRepository;
import com.vti.springdatajpa.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
        private final TransactionRepository transactionRepository;
        private final WalletRepository walletRepository;
        private final UserRepository userRepository;


        public Page<TransactionDTO> getTransactions(String username, Pageable pageable) {
                User user = userRepository.findByUserName(username)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                Wallet wallet = walletRepository.findByUserId(user.getId())
                                .orElseThrow(() -> new RuntimeException("Wallet not found"));

                return transactionRepository.findByWalletId(wallet.getId(), pageable)
                                .map(tx -> {
                                        TransactionDTO dto = new TransactionDTO();
                                        dto.setId(tx.getId());
                                        dto.setType(tx.getType().name());
                                        dto.setAmount(tx.getAmount());
                                        dto.setDate(tx.getCreatedAt());
                                        dto.setStatus(tx.getStatus().name());
                                        // Simplified category/direction
                                        dto.setCategory("General");
                                        dto.setDirection(tx.getDirection() != null ? tx.getDirection().name() : "IN");
                                        return dto;
                                });
        }

        @Transactional
        public void createTransfer(String username, TransferRequest request) {
                User sender = userRepository.findByUserName(username)
                                .orElseThrow(() -> new RuntimeException("Sender not found"));
                Wallet senderWallet = walletRepository.findByUserId(sender.getId())
                                .orElseThrow(() -> new RuntimeException("Sender wallet not found"));

                if (senderWallet.getAvailableBalance() < request.getAmount()) {
                        throw new RuntimeException("Insufficient available balance");
                }

                User receiver = userRepository.findById(request.getToUserId())
                                .orElseThrow(() -> new RuntimeException("Receiver not found"));
                Wallet receiverWallet = walletRepository.findByUserId(receiver.getId())
                                .orElseThrow(() -> new RuntimeException("Receiver wallet not found"));

                senderWallet.setBalance(senderWallet.getBalance() - request.getAmount());
                senderWallet.setAvailableBalance(senderWallet.getAvailableBalance() - request.getAmount());
                walletRepository.save(senderWallet);

                receiverWallet.setBalance(receiverWallet.getBalance() + request.getAmount());
                receiverWallet.setAvailableBalance(receiverWallet.getAvailableBalance() + request.getAmount());
                walletRepository.save(receiverWallet);

                Transaction txOut = new Transaction();
                txOut.setWallet(senderWallet);
                txOut.setAmount(request.getAmount());
                txOut.setType(TransactionType.TRANSFER_OUT);
                txOut.setStatus(TransactionStatus.COMPLETED);
                txOut.setCreatedAt(LocalDateTime.now());
                transactionRepository.save(txOut);

                Transaction txIn = new Transaction();
                txIn.setWallet(receiverWallet);
                txIn.setAmount(request.getAmount());
                txIn.setType(TransactionType.TRANSFER_IN);
                txIn.setStatus(TransactionStatus.COMPLETED);
                txIn.setCreatedAt(LocalDateTime.now());
                transactionRepository.save(txIn);
        }

        @Transactional
        public void createTransaction(String username, TransactionRequest request) {
                if ("topup".equalsIgnoreCase(request.getType())) {
                        User user = userRepository.findByUserName(username)
                                        .orElseThrow(() -> new RuntimeException("User not found"));
                        Wallet wallet = walletRepository.findByUserId(user.getId())
                                        .orElseThrow(() -> new RuntimeException("Wallet not found"));

                        wallet.setBalance(wallet.getBalance() + request.getAmount());
                        wallet.setAvailableBalance(wallet.getAvailableBalance() + request.getAmount());
                        walletRepository.save(wallet);

                        Transaction tx = new Transaction();
                        tx.setWallet(wallet);
                        tx.setAmount(request.getAmount());
                        tx.setType(TransactionType.DEPOSIT);
                        tx.setStatus(TransactionStatus.COMPLETED);
                        tx.setCreatedAt(LocalDateTime.now());
                        transactionRepository.save(tx);
                } else {
                        throw new RuntimeException("Invalid transaction type");
                }
        }

        public Page<TransferHistoryDTO> getTransferHistory(
                Integer walletId,
                TransactionDirection direction,
                LocalDateTime fromDate,
                LocalDateTime toDate,
                Pageable pageable
        ) {

                var transactions = transactionRepository.findTransferHistory(
                        walletId,
                        direction,
                        fromDate,
                        toDate,
                        pageable
                );

                return transactions.map(tx -> {
                        TransferHistoryDTO dto = new TransferHistoryDTO();

                        dto.setId(tx.getId());
                        dto.setAmount(tx.getAmount());
                        dto.setStatus(tx.getStatus().name());
                        dto.setType(tx.getType().name());

                        boolean isOut =
                                tx.getType() == TransactionType.TRANSFER_OUT
                                        || tx.getType() == TransactionType.WITHDRAW;

                        dto.setDirection(isOut ? "OUT" : "IN");

                        dto.setCreatedAt(tx.getCreatedAt());
                        dto.setReferenceId(tx.getReferenceId());

                        dto.setPartnerName(
                                isOut ? "Sent Transfer" : "Received Transfer"
                        );

                        dto.setNote(
                                tx.getMetadata() != null ? tx.getMetadata() : "—"
                        );

                        return dto;
                });
        }
        public TransferDetailDTO getTransferDetail(Integer transferId) {
                Transaction tx = transactionRepository.findById(transferId)
                        .orElseThrow(() -> new RuntimeException("Transaction not found"));

                boolean isOut =
                        tx.getType() == TransactionType.TRANSFER_OUT
                                || tx.getType() == TransactionType.WITHDRAW;

                TransferDetailDTO dto = new TransferDetailDTO();
                dto.setId(tx.getId());
                dto.setAmount(tx.getAmount());
                dto.setStatus(tx.getStatus().name());
                dto.setType(tx.getType().name());
                dto.setDirection(isOut ? "OUT" : "IN");
                dto.setCreatedAt(tx.getCreatedAt());
                dto.setReferenceId(tx.getReferenceId());

                dto.setPartnerName(
                        isOut ? "Sent Transfer" : "Received Transfer"
                );

                dto.setNote(
                        tx.getMetadata() != null ? tx.getMetadata() : "—"
                );

                return dto;
        }

        @Transactional
        public void createTransferByWallet(
                String username,
                Integer fromWalletId,
                Integer toWalletId,
                TransferRequest request
        ) {

                User senderUser = userRepository.findByUserName(username)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                Wallet senderWallet = walletRepository.findByIdForUpdate(fromWalletId)
                        .orElseThrow(() -> new RuntimeException("Sender wallet not found"));

                if (!senderWallet.getUser().getId().equals(senderUser.getId())) {
                        throw new RuntimeException("Invalid sender wallet");
                }

                Wallet receiverWallet = walletRepository.findByIdForUpdate(toWalletId)
                        .orElseThrow(() -> new RuntimeException("Receiver wallet not found"));

                if (senderWallet.getId().equals(receiverWallet.getId())) {
                        throw new RuntimeException("Cannot transfer to yourself");
                }

                if (senderWallet.getAvailableBalance() < request.getAmount()) {
                        throw new RuntimeException("Insufficient balance");
                }

                // Update balances
                senderWallet.setBalance(senderWallet.getBalance() - request.getAmount());
                senderWallet.setAvailableBalance(senderWallet.getAvailableBalance() - request.getAmount());

                receiverWallet.setBalance(receiverWallet.getBalance() + request.getAmount());
                receiverWallet.setAvailableBalance(receiverWallet.getAvailableBalance() + request.getAmount());

                walletRepository.save(senderWallet);
                walletRepository.save(receiverWallet);

                String refId = "TRX-" + System.currentTimeMillis();

                Transaction txOut = new Transaction();
                txOut.setWallet(senderWallet);
                txOut.setAmount(request.getAmount());
                txOut.setType(TransactionType.TRANSFER_OUT);
                txOut.setStatus(TransactionStatus.COMPLETED);
                txOut.setCreatedAt(LocalDateTime.now());
                txOut.setMetadata(request.getNote());
                txOut.setReferenceId(refId);

                Transaction txIn = new Transaction();
                txIn.setWallet(receiverWallet);
                txIn.setAmount(request.getAmount());
                txIn.setType(TransactionType.TRANSFER_IN);
                txIn.setStatus(TransactionStatus.COMPLETED);
                txIn.setCreatedAt(LocalDateTime.now());
                txIn.setMetadata(request.getNote());
                txIn.setReferenceId(refId);

                transactionRepository.save(txOut);
                transactionRepository.save(txIn);
        }

        public List<WalletSelectDTO> searchWalletByPhone(String username, String phone) {
                User currentUser = userRepository.findByUserName(username)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                return walletRepository
                        .searchOtherWalletsByPhone(currentUser.getId(), phone)
                        .stream()
                        .map(w -> new WalletSelectDTO(
                                w.getId(),
                                w.getUser().getFullName(),
                                w.getCode(),
                                w.getAccountNumber()
                        ))
                        .toList();
        }
}
