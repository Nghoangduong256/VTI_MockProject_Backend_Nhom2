package com.vti.springdatajpa.service.impl;

import com.vti.springdatajpa.entity.BankAccount;
import com.vti.springdatajpa.repository.*;
import com.vti.springdatajpa.service.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankAccountServiceImpl implements BankAccountService {
    @Autowired
    private  WalletRepository walletRepo;
    @Autowired
    private  BankAccountRepository bankAccountRepo;


    @Override
    public List<BankAccount> getByUserId(Integer userId) {
        return bankAccountRepo.findByUserId(userId);
    }

    @Override
    public BankAccount getByIdAndUserId(Integer id, Integer userId) {
        return bankAccountRepo.findByIdAndUserId(id, userId);
    }
}
