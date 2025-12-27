package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {
}
