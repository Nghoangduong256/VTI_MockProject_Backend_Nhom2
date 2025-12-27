package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.CardDTO;
import com.vti.springdatajpa.entity.Card;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.repository.CardRepository;
import com.vti.springdatajpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public List<CardDTO> getCards(String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Card> cards = cardRepository.findByUserId(user.getId());

        return cards.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public CardDTO addCard(String username, CardDTO dto) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Card card = new Card();
        card.setCardNumber(dto.getCardNumber());
        card.setCardHolderName(dto.getHolderName());
        card.setExpiryDate(dto.getExpiryDate());
        card.setCvv(dto.getCvv());
        card.setBankName(dto.getBankName());
        card.setType(dto.getType());
        card.setStatus(com.vti.springdatajpa.entity.CardStatus.ACTIVE);
        card.setUser(user);

        Card savedCard = cardRepository.save(card);
        return mapToDTO(savedCard);
    }

    private CardDTO mapToDTO(Card card) {
        CardDTO dto = new CardDTO();
        dto.setId(card.getId());
        dto.setHolderName(card.getCardHolderName());
        dto.setBankName(card.getBankName());
        dto.setType(card.getType());
        dto.setExpiryDate(card.getExpiryDate());
        dto.setStatus(card.getStatus().name());

        String num = card.getCardNumber();
        if (num != null && num.length() >= 4) {
            dto.setLast4(num.substring(num.length() - 4));
            dto.setCardNumber("**** **** **** " + dto.getLast4()); // Masked
        } else {
            dto.setLast4("****");
            dto.setCardNumber(num);
        }

        // Don't return CVV
        return dto;
    }
}
