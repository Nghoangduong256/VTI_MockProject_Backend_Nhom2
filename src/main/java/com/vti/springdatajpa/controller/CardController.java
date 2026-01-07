package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.CardDTO;
import com.vti.springdatajpa.dto.CardDepositRequest;
import com.vti.springdatajpa.dto.CardDepositResponse;
import com.vti.springdatajpa.dto.CardDepositHistoryDTO;
import com.vti.springdatajpa.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;

    @GetMapping
    public ResponseEntity<List<CardDTO>> getCards() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("Card List - JWT identity: " + principal);
        System.out.println("Identity type: " + principal.getClass().getName());
        
        String userName;
        if (principal instanceof com.vti.springdatajpa.entity.User) {
            userName = ((com.vti.springdatajpa.entity.User) principal).getUserName();
            System.out.println("Extracted username from User object: " + userName);
        } else if (principal instanceof String) {
            userName = (String) principal;
            System.out.println("Using string identity: " + userName);
        } else {
            throw new RuntimeException("Unsupported identity type: " + principal.getClass().getName());
        }
        
        return ResponseEntity.ok(cardService.getCards(userName));
    }
    @GetMapping("{userId}/users")
    public ResponseEntity<List<CardDTO>> getCardsByUserId(@PathVariable(value = "userId") Integer userId) {

        return ResponseEntity.ok(cardService.getCards(userId));
    }

    @PostMapping
    public ResponseEntity<CardDTO> addCard(@Valid @RequestBody CardDTO cardDTO) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("Add Card - JWT identity: " + principal);
        
        String userName;
        if (principal instanceof com.vti.springdatajpa.entity.User) {
            userName = ((com.vti.springdatajpa.entity.User) principal).getUserName();
        } else if (principal instanceof String) {
            userName = (String) principal;
        } else {
            throw new RuntimeException("Unsupported identity type: " + principal.getClass().getName());
        }
        
        return ResponseEntity.ok(cardService.addCard(userName, cardDTO));
    }

    @PostMapping("/deposit")
    public ResponseEntity<CardDepositResponse> depositFromCard(@Valid @RequestBody CardDepositRequest request) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("Card Deposit - JWT identity: " + principal);
        
        String userName;
        if (principal instanceof com.vti.springdatajpa.entity.User) {
            userName = ((com.vti.springdatajpa.entity.User) principal).getUserName();
        } else if (principal instanceof String) {
            userName = (String) principal;
        } else {
            throw new RuntimeException("Unsupported identity type: " + principal.getClass().getName());
        }
        
        CardDepositResponse response = cardService.depositFromCard(userName, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/deposit/history")
    public ResponseEntity<List<CardDepositHistoryDTO>> getDepositHistory() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("Card Deposit History - JWT identity: " + principal);
        
        String userName;
        if (principal instanceof com.vti.springdatajpa.entity.User) {
            userName = ((com.vti.springdatajpa.entity.User) principal).getUserName();
        } else if (principal instanceof String) {
            userName = (String) principal;
        } else {
            throw new RuntimeException("Unsupported identity type: " + principal.getClass().getName());
        }
        
        List<CardDepositHistoryDTO> history = cardService.getDepositHistory(userName);
        return ResponseEntity.ok(history);
    }
}
