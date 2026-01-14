package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.TransferDetailDTO;
import com.vti.springdatajpa.dto.TransferHistoryDTO;
import com.vti.springdatajpa.dto.TransferRequest;
import com.vti.springdatajpa.dto.WalletSelectDTO;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.entity.enums.TransactionDirection;
import com.vti.springdatajpa.entity.enums.TransactionFilterType;
import com.vti.springdatajpa.service.WalletTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/E-Wallet/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final WalletTransferService walletTransferService;

    @GetMapping("/wallet/{walletId}/history")
    public ResponseEntity<Map<String, Object>> getTransferHistory(
            @PathVariable Integer walletId,
            @RequestParam(required = false) TransactionDirection direction,
            @RequestParam(required = false) TransactionFilterType filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        LocalDateTime toDate = LocalDateTime.now();
        LocalDateTime fromDate = null;

        if (filter != null) {
            switch (filter) {
                case LAST_30_DAYS -> fromDate = toDate.minusDays(30);
                case LAST_MONTH -> fromDate = toDate.minusMonths(1);
                case LAST_YEAR -> fromDate = toDate.minusYears(1);
            }
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<TransferHistoryDTO> result =
                walletTransferService.getTransferHistory(
                        user.getUserName(),
                        walletId,
                        direction,
                        fromDate,
                        toDate,
                        pageable
                );

        return ResponseEntity.ok(Map.of(
                "content", result.getContent(),
                "totalElements", result.getTotalElements(),
                "totalPages", result.getTotalPages(),
                "currentPage", result.getNumber(),
                "pageSize", result.getSize()
        ));
    }

    @GetMapping("/{transferId}")
    public ResponseEntity<TransferDetailDTO> getTransferDetail(
            @PathVariable Integer transferId
    ) {
        return ResponseEntity.ok(
                walletTransferService.getTransferDetail(transferId)
        );
    }

    @GetMapping("/wallets/search")
    public List<WalletSelectDTO> searchWalletByPhone(
            @RequestParam Map<String, String> params
    ) {
        String phone = params.get("phone");

        if (phone == null || phone.trim().isEmpty()) {
            return List.of();
        }

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        String username = user.getUserName();

        return walletTransferService.searchWalletByPhone(
                username,
                phone.trim()
        );
    }


    @PostMapping
    public ResponseEntity<TransferHistoryDTO> transfer(
            @Valid @RequestBody TransferRequest request
    ) {
        User currentUser = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        TransferHistoryDTO tx =
                walletTransferService.transferByPhone(
                        currentUser.getUserName(),
                        request
                );

        return ResponseEntity.ok(tx);
    }
}
