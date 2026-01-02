package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.*;
import com.vti.springdatajpa.entity.Wallet;
import com.vti.springdatajpa.service.DepositService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/E-Wallet/deposits")
@RequiredArgsConstructor
public class DepositController {
    private final DepositService depositService;
    private final ModelMapper modelMapper;
    // nạp tiền vào ví
    @PostMapping
    public DepositResponse deposit(@RequestBody DepositRequest request) {

        Wallet wallet = depositService.deposit(request);

        return new DepositResponse(
                "Deposit successful",
                wallet.getBalance()
        );
    }
    // lấy ví theo user id
    @GetMapping("/wallet-by-username/{userName}")
    public WalletSimpleDTO getWalletByUserName(@PathVariable String userName) {
        Wallet wallet = depositService.getWalletByUserName(userName);

        return new WalletSimpleDTO(
                wallet.getId(),
                wallet.getBalance()
        );
    }

    // laays ví theo id để hển thị balance hiện tại
    @GetMapping("/wallet/{id}")
    public WalletInDeposit getWalletById(@PathVariable Integer id) {
       Wallet wallet = depositService.getWalletById(id);
         return modelMapper.map(wallet, WalletInDeposit.class);
    }
    // lấy lịch sử nạp tiền gần đây theo id ví
    @GetMapping("/wallet/{id}/recent-deposits")
    public List<DepositHistoryDTO> getRecentDeposits(@PathVariable Integer id) {
        return depositService.getRecentDeposits(id);
    }


}
