package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.Transaction;
import com.vti.springdatajpa.entity.enums.TransactionDirection;
import com.vti.springdatajpa.entity.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    // dùng cho chống double-submit / retry
    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);

    // dùng cho lịch sử nạp tiền (top N)
    List<Transaction> findTop5ByWallet_IdAndTypeOrderByCreatedAtDesc(
            Integer walletId,
            TransactionType type
    );

    // dùng khi cần full history (không limit)
    List<Transaction> findByWallet_IdAndTypeOrderByCreatedAtDesc(
            Integer walletId,
            TransactionType type
    );
    
    // dùng cho phân trang giao dịch
    Page<Transaction> findByWalletId(Integer walletId, Pageable pageable);
    
    // dùng cho incoming transactions (direction = IN)
    @Query("SELECT t FROM Transaction t WHERE t.wallet.id = ?1 AND t.direction = ?2 ORDER BY t.createdAt DESC")
    List<Transaction> findIncomingTransactions(Integer walletId, TransactionDirection direction);
}
