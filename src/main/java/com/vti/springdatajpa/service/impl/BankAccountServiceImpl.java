package com.vti.springdatajpa.service.impl;

import com.vti.springdatajpa.entity.BankAccount;
import com.vti.springdatajpa.repository.*;
import com.vti.springdatajpa.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {
    private final WalletRepository walletRepo;
    private final BankAccountRepository bankAccountRepo;


    @Override
    public List<BankAccount> getByUserId(Integer userId) {
        return bankAccountRepo.findByUserId(userId);
    }

    @Override
    public BankAccount getByIdAndUserId(Integer id, Integer userId) {
        return bankAccountRepo.findByIdAndUserId(id, userId);
    }
}
