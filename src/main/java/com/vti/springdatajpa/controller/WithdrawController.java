package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.WithdrawRequest;
import com.vti.springdatajpa.dto.WithdrawResponse;
import com.vti.springdatajpa.entity.Transaction;
import com.vti.springdatajpa.entity.enums.TransactionType;
import com.vti.springdatajpa.repository.TransactionRepository;
import com.vti.springdatajpa.service.WithdrawService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/")
public class WithdrawController {

    private final WithdrawService withdrawService;
    private final TransactionRepository txRepo;

    public WithdrawController(WithdrawService withdrawService, TransactionRepository txRepo) {
        this.withdrawService = withdrawService;
        this.txRepo = txRepo;
    }

    @PostMapping("/wallets/{walletId}/withdraw")
    public WithdrawResponse withdraw(@PathVariable Integer walletId,
                                     @RequestBody WithdrawRequest req,
                                     @RequestHeader("Idempotency-Key") String idempotencyKey) {
        Integer userId = 1; // bạn thay bằng JWT principal
        System.out.println("Wallet ID: " + walletId);
        return withdrawService.createWithdraw(userId, walletId, req, idempotencyKey);
    }
//
//    @GetMapping("/transactions")
//    public List<Transaction> list(@RequestParam TransactionType type) {
//        Integer userId = 1;
//
//        return txRepo.findAll().stream().filter(t -> t.getType() == type).toList();
//    }

//    @PostMapping("/bank/webhook/withdraw")
//    public void bankWebhook(@RequestBody BankWebhookRequest req) {
//        withdrawService.handleBankWebhook(req.transactionId(), req.bankReference(), req.status());
//    }
}
