package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bank-account")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @GetMapping("")
    public ResponseEntity<?> getByUserId(@RequestParam(value = "userId") Integer userId) {
//        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(bankAccountService.getByUserId(userId));
    }

}
