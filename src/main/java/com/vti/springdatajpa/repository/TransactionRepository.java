package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.Transaction;
import com.vti.springdatajpa.entity.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);
    List<Transaction> findByWalletIdAndTypeOrderByCreatedAtDesc(Integer walletId, TransactionType type);
}