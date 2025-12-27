package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    // Find transactions for a specific wallet
    Page<Transaction> findByWalletId(Integer walletId, Pageable pageable);

    // Find transactions within a date range for a wallet
    @Query("SELECT t FROM Transaction t WHERE t.wallet.id = :walletId AND t.createdAt BETWEEN :startDate AND :endDate")
    List<Transaction> findByWalletIdAndCreatedAtBetween(@Param("walletId") Integer walletId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Recent transactions (simplified by using pageable in service)
}
