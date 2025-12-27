package com.vti.springdatajpa.service;

import com.vti.springdatajpa.entity.BankAccount;

import java.util.List;

public interface BankAccountService {
    List<BankAccount> getByUserId(Integer userId);
    BankAccount getByIdAndUserId(Integer id, Integer userId);
}
