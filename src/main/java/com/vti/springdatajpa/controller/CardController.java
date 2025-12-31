package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.CardDTO;
import com.vti.springdatajpa.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;

    @GetMapping
    public ResponseEntity<List<CardDTO>> getCards() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(cardService.getCards(username));
    }

    @org.springframework.web.bind.annotation.PostMapping
    public ResponseEntity<CardDTO> addCard(@org.springframework.web.bind.annotation.RequestBody CardDTO cardDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(cardService.addCard(username, cardDTO));
    }
}
