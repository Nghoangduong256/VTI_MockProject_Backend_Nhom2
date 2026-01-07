package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.WithdrawRequest;
import com.vti.springdatajpa.dto.WithdrawResponse;
import com.vti.springdatajpa.entity.Transaction;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.entity.enums.TransactionType;
import com.vti.springdatajpa.repository.TransactionRepository;
import com.vti.springdatajpa.service.WithdrawService;
import org.springframework.security.core.context.SecurityContextHolder;
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
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return withdrawService.createWithdraw(user.getId(), walletId, req, idempotencyKey);
    }
}
