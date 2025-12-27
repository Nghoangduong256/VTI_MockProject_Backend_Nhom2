package com.vti.springdatajpa.service;


import com.vti.springdatajpa.dto.WithdrawRequest;
import com.vti.springdatajpa.dto.WithdrawResponse;

public interface WithdrawService {

    WithdrawResponse createWithdraw(Integer userId, Integer walletId, WithdrawRequest req, String idempotencyKey);
    void handleBankWebhook(Integer transactionId, String bankReference, String status);
}
