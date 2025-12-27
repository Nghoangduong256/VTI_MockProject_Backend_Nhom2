package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.service.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bank-account")
public class BankAccountController {

    @Autowired
    private  BankAccountService bankAccountService;

    @GetMapping("")
    public ResponseEntity<?> getByUserId(@RequestParam(value = "userId") Integer userId) {
//        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(bankAccountService.getByUserId(userId));
    }

}
