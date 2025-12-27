package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BankAccountRepository extends JpaRepository<BankAccount, Integer> {
    List<BankAccount> findByUserId(Integer userId);
}
