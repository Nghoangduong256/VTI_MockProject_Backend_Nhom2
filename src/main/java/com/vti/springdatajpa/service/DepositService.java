package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.DepositHistoryDTO;
import com.vti.springdatajpa.dto.DepositRequest;
import com.vti.springdatajpa.entity.Wallet;

import java.util.List;

public interface DepositService {
    public Wallet deposit(DepositRequest request);
    Wallet getWalletById(Integer id);
    public List<DepositHistoryDTO> getRecentDeposits(Integer walletId);
}
