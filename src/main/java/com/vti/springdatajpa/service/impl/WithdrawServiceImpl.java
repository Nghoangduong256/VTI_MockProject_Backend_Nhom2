package com.vti.springdatajpa.service.impl;

import com.vti.springdatajpa.dto.WithdrawRequest;
import com.vti.springdatajpa.dto.WithdrawResponse;
import com.vti.springdatajpa.entity.*;
import com.vti.springdatajpa.entity.enums.*;
import com.vti.springdatajpa.repository.*;
import com.vti.springdatajpa.service.WithdrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class WithdrawServiceImpl implements WithdrawService {

    @Autowired
    private WalletRepository walletRepo;

    @Autowired
    private BankAccountRepository bankAccountRepo;
    @Autowired
    private TransactionRepository txRepo;
    @Autowired
    private BankTransferRepository bankTransferRepo;
    @Autowired
    private BalanceChangeLogRepository balanceChangeLogRepo;
    @Autowired
    private CardRepository cardRepository;

    // Fee demo: 0.5%
    private BigDecimal calcFee(BigDecimal amount) {
        return amount.multiply(new BigDecimal("0.05"));
    }

    @Transactional
    public WithdrawResponse createWithdraw(Integer userId, Integer walletId, WithdrawRequest req, String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank())
            throw new IllegalArgumentException("Missing Idempotency-Key");

        // idempotent
        var existing = txRepo.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            var tx = existing.get();
            // Trả lại response dựa trên tx đã có
            return new WithdrawResponse(
                    tx.getId(), tx.getStatus().name(), tx.getAmount(), tx.getFee(),
                    tx.getAmount() + tx.getFee(),
                    tx.getBalanceBefore(),
                    null, null,
                    tx.getCreatedAt()
            );
        }

        Card card = cardRepository.findByIdAndUserId(req.bankAccountId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));

        // check wallet belongs user + lock
        Optional.ofNullable(walletRepo.findByIdAndUserId(walletId, userId))
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        var wallet = walletRepo.findByIdForUpdate(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        if (wallet.getStatus() != WalletStatus.ACTIVE)
            throw new IllegalStateException("Wallet not ACTIVE");

        var amount = req.amount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Invalid amount");

        var fee = calcFee(amount);
        var totalDebit = amount.add(fee);

        var availableBefore = wallet.getAvailableBalance();
        if (availableBefore.compareTo(totalDebit.doubleValue()) < 0)
            throw new IllegalStateException("Insufficient available balance");

        // HOLD: trừ availableBalance ngay
        wallet.setAvailableBalance(availableBefore - totalDebit.doubleValue());
        walletRepo.save(wallet);

        var tx = new Transaction();
        tx.setWallet(wallet);
        tx.setType(TransactionType.WITHDRAW);
        tx.setDirection(TransactionDirection.OUT);
        tx.setAmount(amount.doubleValue());
        tx.setFee(fee.doubleValue());
        tx.setBalanceBefore(wallet.getAvailableBalance());        // ✅ Đúng
        tx.setBalanceAfter(wallet.getAvailableBalance());         // ✅ Đúngtrừ balance ở bước PENDING
        tx.setStatus(TransactionStatus.PENDING);
        tx.setIdempotencyKey(idempotencyKey);

        tx.setMetadata("""
                  {"note": %s, "cardId": "%s", "bankCode": "%s"}
                """.formatted(
                req.note() == null ? "null" : ("\"" + req.note().replace("\"", "\\\"") + "\""),
                card.getId(), card.getBankName()
        ));

        txRepo.save(tx);

        card.setBalanceCard(card.getBalanceCard() + req.amount().doubleValue());
        cardRepository.save(card);

        var bcl = new BalanceChangeLog();
        bcl.setTransaction(tx);
        bcl.setDelta(totalDebit.negate().doubleValue()); // hold
        bcl.setBalanceBefore(availableBefore);
        bcl.setBalanceAfter(wallet.getAvailableBalance());
        balanceChangeLogRepo.save(bcl);

        return new WithdrawResponse(
                tx.getId(),
                tx.getStatus().name(),
                amount.doubleValue(),
                fee.doubleValue(),
                totalDebit.doubleValue(),
                tx.getBalanceBefore(),
                availableBefore,
                wallet.getAvailableBalance(),
                tx.getCreatedAt()
        );
    }

    @Transactional
    public void handleBankWebhook(Integer transactionId, String bankReference, String status) {
        var tx = txRepo.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        if (tx.getType() != TransactionType.WITHDRAW) return;
        if (tx.getStatus() != TransactionStatus.PENDING) return;

        var wallet = walletRepo.findByIdForUpdate(tx.getWallet().getId())
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        var totalDebit = tx.getAmount() + tx.getFee();

        if ("SUCCESS".equalsIgnoreCase(status)) {
            // Trừ balance thật
var balanceBefore = wallet.getAvailableBalance();          // ✅ Đúng
wallet.setBalance(balanceBefore - totalDebit);             // ✅ Đúng
wallet.setAvailableBalance(balanceBefore - totalDebit);    // ✅ Thêm
            walletRepo.save(wallet);

            tx.setStatus(TransactionStatus.COMPLETED);
            tx.setReferenceId(bankReference);
            tx.setBalanceAfter(wallet.getAvailableBalance());
            txRepo.save(tx);

            // log ledger trừ balance (nếu bạn muốn tách ledger hold vs settle rõ hơn)
            var bcl = new BalanceChangeLog();
            bcl.setWallet(wallet);
            bcl.setTransaction(tx);
            bcl.setDelta(totalDebit);
            bcl.setBalanceBefore(balanceBefore);
            bcl.setBalanceAfter(wallet.getAvailableBalance());
            balanceChangeLogRepo.save(bcl);

        } else {
            // FAIL: trả lại availableBalance
            var availableBefore = wallet.getAvailableBalance();
            wallet.setAvailableBalance(availableBefore - totalDebit);
            walletRepo.save(wallet);

            tx.setStatus(TransactionStatus.FAILED);
            tx.setReferenceId(bankReference);
            txRepo.save(tx);

            var bcl = new BalanceChangeLog();
            bcl.setWallet(wallet);
            bcl.setTransaction(tx);
            bcl.setDelta(totalDebit); // release hold
            bcl.setBalanceBefore(availableBefore);
            bcl.setBalanceAfter(wallet.getAvailableBalance());
            balanceChangeLogRepo.save(bcl);
        }

        // update bank transfer nếu bạn muốn truy theo transactionId
        // (mình để giản lược: có thể query bankTransfer theo transactionId rồi update)
    }
}
