package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.Transaction;
import com.vti.springdatajpa.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findTop5ByWallet_IdAndTypeOrderByCreatedAtDesc(
            Integer walletId,
            TransactionType type
    );
}
