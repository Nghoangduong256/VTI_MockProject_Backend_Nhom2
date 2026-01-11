package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.SpendingSummaryResponse;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.service.SpendingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/spending")
@RequiredArgsConstructor
public class SpendingController {
    private final SpendingService spendingService;

    @GetMapping("/summary")
    public ResponseEntity<SpendingSummaryResponse> getSpendingSummary(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(
                spendingService.getSpendingSummary(user.getId())
        );
    }


}
