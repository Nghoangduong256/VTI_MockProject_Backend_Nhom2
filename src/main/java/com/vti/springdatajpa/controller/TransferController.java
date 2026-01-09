package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.TransferDetailDTO;
import com.vti.springdatajpa.dto.TransferHistoryDTO;
import com.vti.springdatajpa.dto.TransferRequest;
import com.vti.springdatajpa.dto.WalletSelectDTO;
import com.vti.springdatajpa.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/E-Wallet/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransactionService transactionService;

    @GetMapping("/wallet/{walletId}/history")
    public ResponseEntity<Map<String, Object>> getTransferHistory(
            @PathVariable Integer walletId,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,

            @RequestParam(required = false) String direction,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime toDate
    ) {

        var directionEnum = direction != null
                ? Enum.valueOf(
                com.vti.springdatajpa.entity.enums.TransactionDirection.class,
                direction
        )
                : null;

        var pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<TransferHistoryDTO> result =
                transactionService.getTransferHistory(
                        walletId,
                        directionEnum,
                        fromDate,
                        toDate,
                        pageable
                );

        var response = Map.of(
                "content", result.getContent(),
                "totalElements", result.getTotalElements(),
                "totalPages", result.getTotalPages(),
                "currentPage", result.getNumber(),
                "pageSize", result.getSize()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/wallet/{walletId}")
    public ResponseEntity<?> getWallet(@PathVariable Integer walletId) {
        return ResponseEntity.ok(Map.of("walletId", walletId));
    }

    @GetMapping("/{transferId}")
    public ResponseEntity<TransferDetailDTO> getTransferDetail(
            @PathVariable Integer transferId
    ) {
        return ResponseEntity.ok(
                transactionService.getTransferDetail(transferId)
        );
    }
    @GetMapping("/wallets/search")
    public List<WalletSelectDTO> searchWalletByPhone(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam String phone
    ) {
        return transactionService.searchWalletByPhone(user.getUsername(), phone);
    }
}
