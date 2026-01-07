package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.SpendingAnalyticsDTO;
import com.vti.springdatajpa.dto.WalletSummaryDTO;
import com.vti.springdatajpa.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/wallet/summary")
    public ResponseEntity<WalletSummaryDTO> getWalletSummary(
            @RequestParam(defaultValue = "current") String period) {
        
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("Wallet Summary - JWT identity: " + principal);
        
        String userName;
        if (principal instanceof com.vti.springdatajpa.entity.User) {
            userName = ((com.vti.springdatajpa.entity.User) principal).getUserName();
        } else if (principal instanceof String) {
            userName = (String) principal;
        } else {
            throw new RuntimeException("Unsupported identity type: " + principal.getClass().getName());
        }
        
        WalletSummaryDTO summary = dashboardService.getWalletSummary(userName, period);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/analytics/spending")
    public ResponseEntity<List<SpendingAnalyticsDTO>> getSpendingAnalytics(
            @RequestParam(defaultValue = "7days") String range) {
        
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("Spending Analytics - JWT identity: " + principal);
        
        String userName;
        if (principal instanceof com.vti.springdatajpa.entity.User) {
            userName = ((com.vti.springdatajpa.entity.User) principal).getUserName();
        } else if (principal instanceof String) {
            userName = (String) principal;
        } else {
            throw new RuntimeException("Unsupported identity type: " + principal.getClass().getName());
        }
        
        List<SpendingAnalyticsDTO> analytics = dashboardService.getSpendingAnalytics(userName, range);
        return ResponseEntity.ok(analytics);
    }
}
