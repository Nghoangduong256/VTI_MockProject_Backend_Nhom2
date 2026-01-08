package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.*;
import com.vti.springdatajpa.entity.Card;
import com.vti.springdatajpa.entity.CardWithdraw;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.entity.Wallet;
import com.vti.springdatajpa.repository.CardRepository;
import com.vti.springdatajpa.repository.CardWithdrawRepository;
import com.vti.springdatajpa.repository.UserRepository;
import com.vti.springdatajpa.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardWithdrawService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final CardWithdrawRepository cardWithdrawRepository;

    @Transactional
    public CardWithdrawResponse withdrawFromCard(String username, CardWithdrawRequest request) {
        User user = findUserByUsernameOrEmail(username);
        
        // Validate input parameters
        if (request.getCardId() == null) {
            return createErrorResponse("Card ID is required", null);
        }
        
        if (request.getAmount() == null || request.getAmount() <= 0) {
            return createErrorResponse("Amount must be greater than 0", null);
        }
        
        if (request.getAmount() > 5000000.00) {
            return createErrorResponse("Maximum withdraw amount is 5,000,000 USD per transaction", null);
        }
        
        // Validate card ownership and existence
        Card card = cardRepository.findByIdAndUserId(request.getCardId(), user.getId())
                .orElseThrow(() -> new RuntimeException("Card not found or doesn't belong to user"));
        
        // Validate card status
        if (card.getStatus() != com.vti.springdatajpa.entity.enums.CardStatus.ACTIVE) {
            return createErrorResponse("Card is not active. Please contact support.", card.getId());
        }
        
        // Validate card balance
        if (card.getBalanceCard() < request.getAmount()) {
            CardWithdrawResponse response = createErrorResponse(
                "Insufficient card balance. Available: " + card.getBalanceCard() + " USD", 
                card.getId()
            );
            
            // Save failed withdraw record
            saveWithdrawRecord(card, user, request.getAmount(), request.getDescription(), 
                com.vti.springdatajpa.entity.enums.CardWithdrawStatus.FAILED);
            
            return response;
        }

        // Get user wallet
        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        // Validate wallet status
        if (wallet.getStatus() != com.vti.springdatajpa.entity.enums.WalletStatus.ACTIVE) {
            return createErrorResponse("Wallet is not active. Please contact support.", card.getId());
        }

        // Store previous balances
        Double previousCardBalance = card.getBalanceCard();
        Double previousWalletBalance = wallet.getAvailableBalance();

        try {
            // Update balances
            card.setBalanceCard(card.getBalanceCard() - request.getAmount());
            wallet.setAvailableBalance(wallet.getAvailableBalance() - request.getAmount());

            // Save changes
            cardRepository.save(card);
            walletRepository.save(wallet);

            // Save successful withdraw record
            CardWithdraw savedWithdraw = saveWithdrawRecord(card, user, request.getAmount(), 
                request.getDescription(), com.vti.springdatajpa.entity.enums.CardWithdrawStatus.SUCCESS);

            // Create success response
            CardWithdrawResponse response = new CardWithdrawResponse();
            response.setTransactionId(savedWithdraw.getId());
            response.setCardId(card.getId());
            response.setCardNumber(maskCardNumber(card.getCardNumber()));
            response.setAmount(request.getAmount());
            response.setPreviousCardBalance(previousCardBalance);
            response.setNewCardBalance(card.getBalanceCard());
            response.setPreviousWalletBalance(previousWalletBalance);
            response.setNewWalletBalance(wallet.getAvailableBalance());
            response.setDescription(request.getDescription());
            response.setTimestamp(LocalDateTime.now());
            response.setStatus("SUCCESS");
            response.setMessage("Withdraw successful");

            return response;
            
        } catch (Exception e) {
            // Rollback on error (handled by @Transactional)
            CardWithdrawResponse response = createErrorResponse(
                "Withdraw failed: " + e.getMessage(), 
                card.getId()
            );
            
            // Save failed withdraw record
            saveWithdrawRecord(card, user, request.getAmount(), request.getDescription(), 
                com.vti.springdatajpa.entity.enums.CardWithdrawStatus.FAILED);
            
            return response;
        }
    }

    public List<CardWithdrawHistoryDTO> getWithdrawHistory(String username) {
        User user = findUserByUsernameOrEmail(username);
        
        List<CardWithdraw> withdraws = cardWithdrawRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        
        return withdraws.stream()
                .map(this::mapToWithdrawHistoryDTO)
                .collect(Collectors.toList());
    }

    private CardWithdrawResponse createErrorResponse(String message, Integer cardId) {
        CardWithdrawResponse response = new CardWithdrawResponse();
        response.setTransactionId(null);
        response.setCardId(cardId);
        response.setCardNumber(cardId != null ? maskCardNumber(getCardNumberById(cardId)) : null);
        response.setAmount(null);
        response.setPreviousCardBalance(null);
        response.setNewCardBalance(null);
        response.setPreviousWalletBalance(null);
        response.setNewWalletBalance(null);
        response.setDescription(null);
        response.setTimestamp(LocalDateTime.now());
        response.setStatus("FAILED");
        response.setMessage(message);
        return response;
    }

    private String getCardNumberById(Integer cardId) {
        if (cardId == null) return null;
        return cardRepository.findById(cardId)
                .map(Card::getCardNumber)
                .orElse("****");
    }

    private User findUserByUsernameOrEmail(Object identity) {
        if (identity instanceof com.vti.springdatajpa.entity.User) {
            return (com.vti.springdatajpa.entity.User) identity;
        } else if (identity instanceof String) {
            String usernameOrEmail = (String) identity;
            return userRepository.findByUserName(usernameOrEmail)
                    .orElseGet(() -> userRepository.findByEmail(usernameOrEmail)
                            .orElseThrow(() -> new RuntimeException("User not found")));
        } else {
            throw new RuntimeException("Unsupported identity type: " + identity.getClass().getName());
        }
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

    private CardWithdraw saveWithdrawRecord(Card card, User user, Double amount, 
            String description, com.vti.springdatajpa.entity.enums.CardWithdrawStatus status) {
        CardWithdraw withdraw = new CardWithdraw();
        withdraw.setCard(card);
        withdraw.setUser(user);
        withdraw.setAmount(amount);
        withdraw.setDescription(description);
        withdraw.setCreatedAt(LocalDateTime.now());
        withdraw.setStatus(status);
        
        return cardWithdrawRepository.save(withdraw);
    }

    private CardWithdrawHistoryDTO mapToWithdrawHistoryDTO(CardWithdraw withdraw) {
        CardWithdrawHistoryDTO dto = new CardWithdrawHistoryDTO();
        dto.setTransactionId(withdraw.getId());
        dto.setCardId(withdraw.getCard().getId());
        dto.setCardNumber(maskCardNumber(withdraw.getCard().getCardNumber()));
        dto.setBankName(withdraw.getCard().getBankName());
        dto.setAmount(withdraw.getAmount());
        dto.setDescription(withdraw.getDescription());
        dto.setTimestamp(withdraw.getCreatedAt());
        dto.setStatus(withdraw.getStatus().name());
        return dto;
    }
}
