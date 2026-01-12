package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.TransferDetailDTO;
import com.vti.springdatajpa.dto.TransferHistoryDTO;
import com.vti.springdatajpa.dto.TransferRequest;
import com.vti.springdatajpa.dto.WalletSelectDTO;
import com.vti.springdatajpa.entity.Transaction;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.entity.Wallet;
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
public class WalletTransferService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    /* =========================
       SEARCH WALLET
       ========================= */
    public List<WalletSelectDTO> searchWalletByPhone(String username, String phone) {

        User currentUser = userRepository.findByUserName(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return walletRepository.searchOtherWalletsByPhone(
                currentUser.getId(),
                phone
        );
    }

    /* =========================
       TRANSFER MONEY
       ========================= */
    @Transactional
    public TransferHistoryDTO transferByPhone(
            String username,
            TransferRequest request
    ) {
        validateTransferRequest(request);

        User sender = userRepository.findByUserName(username)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));

        if (sender.getId().equals(request.getToUserId())) {
            throw new IllegalArgumentException("Cannot transfer to yourself");
        }

        Wallet senderWallet = walletRepository.findByUserId(sender.getId())
                .orElseThrow(() -> new IllegalArgumentException("Sender wallet not found"));

        User receiver = userRepository.findById(request.getToUserId())
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        Wallet receiverWallet = walletRepository.findByUserId(receiver.getId())
                .orElseThrow(() -> new IllegalArgumentException("Receiver wallet not found"));

        // Lock wallets
        Wallet lockedSenderWallet = walletRepository.findByIdForUpdate(senderWallet.getId())
                .orElseThrow(() -> new IllegalArgumentException("Sender wallet lock failed"));

        Wallet lockedReceiverWallet = walletRepository.findByIdForUpdate(receiverWallet.getId())
                .orElseThrow(() -> new IllegalArgumentException("Receiver wallet lock failed"));

        Double amount = request.getAmount();

        if (lockedSenderWallet.getAvailableBalance() < amount) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        String referenceId = "TRX-" + System.currentTimeMillis();
        LocalDateTime now = LocalDateTime.now();

        /* UPDATE BALANCE */
        lockedSenderWallet.setBalance(lockedSenderWallet.getBalance() - amount);
        lockedSenderWallet.setAvailableBalance(
                lockedSenderWallet.getAvailableBalance() - amount
        );

        lockedReceiverWallet.setBalance(lockedReceiverWallet.getBalance() + amount);
        lockedReceiverWallet.setAvailableBalance(
                lockedReceiverWallet.getAvailableBalance() + amount
        );

        walletRepository.save(lockedSenderWallet);
        walletRepository.save(lockedReceiverWallet);

        /* TRANSACTION - SENDER */
        Transaction txOut = buildTransaction(
                lockedSenderWallet,
                amount,
                TransactionDirection.OUT,
                TransactionType.TRANSFER_OUT,
                referenceId,
                request.getNote(),
                now
        );

        /* TRANSACTION - RECEIVER */
        Transaction txIn = buildTransaction(
                lockedReceiverWallet,
                amount,
                TransactionDirection.IN,
                TransactionType.TRANSFER_IN,
                referenceId,
                request.getNote(),
                now
        );

        transactionRepository.save(txOut);
        transactionRepository.save(txIn);

        // trả về history cho người gửi
        return mapToHistoryDTO(txOut, receiver.getFullName());
    }

    /* =========================
       TRANSFER HISTORY
       ========================= */
    public Page<TransferHistoryDTO> getTransferHistory(
            String username,
            Integer walletId,
            TransactionDirection direction,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Pageable pageable
    ) {

        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        walletRepository.findByIdAndUserId(walletId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Wallet not belong to user"));

        return transactionRepository
                .findTransferHistory(walletId, direction, fromDate, toDate, pageable)
                .map(tx -> mapToHistoryDTO(
                        tx,
                        tx.getDirection() == TransactionDirection.OUT
                                ? "Sent money"
                                : "Received money"
                ));
    }

    /* =========================
       TRANSFER DETAIL
       ========================= */
    public TransferDetailDTO getTransferDetail(Integer transferId) {

        Transaction tx = transactionRepository.findById(transferId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        boolean isOut = tx.getDirection() == TransactionDirection.OUT;

        TransferDetailDTO dto = new TransferDetailDTO();
        dto.setId(tx.getId());
        dto.setAmount(tx.getAmount());
        dto.setStatus(tx.getStatus().name());
        dto.setType(tx.getType().name());
        dto.setDirection(isOut ? "OUT" : "IN");
        dto.setCreatedAt(tx.getCreatedAt());
        dto.setReferenceId(tx.getReferenceId());
        dto.setPartnerName(isOut ? "Sent Transfer" : "Received Transfer");
        dto.setNote(tx.getMetadata() != null ? tx.getMetadata() : "—");

        return dto;
    }

    /* =========================
       PRIVATE HELPERS
       ========================= */

    private void validateTransferRequest(TransferRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is missing");
        }
        if (request.getToUserId() == null) {
            throw new IllegalArgumentException("toUserId is required");
        }
        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new IllegalArgumentException("Invalid amount");
        }
    }

    private Transaction buildTransaction(
            Wallet wallet,
            Double amount,
            TransactionDirection direction,
            TransactionType type,
            String referenceId,
            String note,
            LocalDateTime createdAt
    ) {
        Transaction tx = new Transaction();
        tx.setWallet(wallet);
        tx.setAmount(amount);
        tx.setDirection(direction);
        tx.setType(type);
        tx.setStatus(TransactionStatus.COMPLETED);
        tx.setReferenceId(referenceId);
        tx.setMetadata(note);
        tx.setCreatedAt(createdAt);
        return tx;
    }

    private TransferHistoryDTO mapToHistoryDTO(
            Transaction tx,
            String partnerName
    ) {
        TransferHistoryDTO dto = new TransferHistoryDTO();
        dto.setId(tx.getId());
        dto.setAmount(tx.getAmount());
        dto.setStatus(tx.getStatus().name());
        dto.setType(tx.getType().name());
        dto.setDirection(tx.getDirection().name());
        dto.setCreatedAt(tx.getCreatedAt());
        dto.setReferenceId(tx.getReferenceId());
        dto.setPartnerName(partnerName);
        dto.setNote(tx.getMetadata());
        return dto;
    }
}
