package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.BankTransfer;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BankTransferRepository extends JpaRepository<BankTransfer, Integer> {}