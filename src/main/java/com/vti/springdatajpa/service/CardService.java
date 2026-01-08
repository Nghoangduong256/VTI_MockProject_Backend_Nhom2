package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.CardDepositHistoryDTO;
import com.vti.springdatajpa.dto.CardDTO;
import com.vti.springdatajpa.dto.CardDepositRequest;
import com.vti.springdatajpa.dto.CardDepositResponse;
import com.vti.springdatajpa.entity.Card;
import com.vti.springdatajpa.entity.CardDeposit;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.entity.Wallet;
import com.vti.springdatajpa.repository.CardDepositRepository;
import com.vti.springdatajpa.repository.CardRepository;
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
public class CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final CardDepositRepository cardDepositRepository;

    public List<CardDTO> getCards(String username) {
        User user = findUserByUsernameOrEmail(username);

        List<Card> cards = cardRepository.findByUserId(user.getId());

        return cards.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<CardDTO> getCards(int id) {

        List<Card> cards = cardRepository.findByUserId(id);

        return cards.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public CardDTO addCard(String username, CardDTO dto) {
        User user = findUserByUsernameOrEmail(username);

        Card card = new Card();
        card.setCardNumber(dto.getCardNumber());
        card.setCardHolderName(dto.getHolderName());
        card.setExpiryDate(dto.getExpiryDate());
        card.setCvv(dto.getCvv());
        card.setBankName(dto.getBankName());
        card.setType(dto.getType());
        card.setStatus(com.vti.springdatajpa.entity.enums.CardStatus.ACTIVE);
        card.setBalanceCard(100000.0); // Default balance
        card.setUser(user);

        Card savedCard = cardRepository.save(card);
        return mapToDTO(savedCard);
    }

    @Transactional
    public CardDepositResponse depositFromCard(String username, CardDepositRequest request) {
        User user = findUserByUsernameOrEmail(username);
        
        // Validate input parameters
        if (request.getCardId() == null) {
            return createErrorResponse("Card ID is required", null);
        }
        
        if (request.getAmount() == null || request.getAmount() <= 0) {
            return createErrorResponse("Amount must be greater than 0", null);
        }
        
        if (request.getAmount() > 5000000.00) {
            return createErrorResponse("Maximum deposit amount is 5,000,000 USD per transaction", null);
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
            CardDepositResponse response = createErrorResponse(
                "Insufficient card balance. Available: " + card.getBalanceCard() + " USD", 
                card.getId()
            );
            
            // Save failed deposit record
            saveDepositRecord(card, user, request.getAmount(), request.getDescription(), 
                CardDeposit.DepositStatus.FAILED);
            
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
            wallet.setAvailableBalance(wallet.getAvailableBalance() + request.getAmount());

            // Save changes
            cardRepository.save(card);
            walletRepository.save(wallet);

            // Save successful deposit record
            CardDeposit savedDeposit = saveDepositRecord(card, user, request.getAmount(), 
                request.getDescription(), CardDeposit.DepositStatus.SUCCESS);

            // Create success response
            CardDepositResponse response = new CardDepositResponse();
            response.setTransactionId(savedDeposit.getId());
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
            response.setMessage("Deposit successful");

            return response;
            
        } catch (Exception e) {
            // Rollback on error (handled by @Transactional)
            CardDepositResponse response = createErrorResponse(
                "Deposit failed: " + e.getMessage(), 
                card.getId()
            );
            
            // Save failed deposit record
            saveDepositRecord(card, user, request.getAmount(), request.getDescription(), 
                CardDeposit.DepositStatus.FAILED);
            
            return response;
        }
    }

    private CardDepositResponse createErrorResponse(String message, Integer cardId) {
        CardDepositResponse response = new CardDepositResponse();
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

    public List<CardDepositHistoryDTO> getDepositHistory(String username) {
        User user = findUserByUsernameOrEmail(username);
        
        List<CardDeposit> deposits = cardDepositRepository.findByUserIdOrderByTimestampDesc(user.getId());
        
        return deposits.stream()
                .map(this::mapToDepositHistoryDTO)
                .collect(Collectors.toList());
    }

    private CardDeposit saveDepositRecord(Card card, User user, Double amount, 
            String description, CardDeposit.DepositStatus status) {
        CardDeposit deposit = new CardDeposit();
        deposit.setCard(card);
        deposit.setUser(user);
        deposit.setAmount(amount);
        deposit.setDescription(description);
        deposit.setTimestamp(LocalDateTime.now());
        deposit.setStatus(status);
        
        return cardDepositRepository.save(deposit);
    }

    private CardDepositHistoryDTO mapToDepositHistoryDTO(CardDeposit deposit) {
        CardDepositHistoryDTO dto = new CardDepositHistoryDTO();
        dto.setTransactionId(deposit.getId());
        dto.setCardId(deposit.getCard().getId());
        dto.setCardNumber(maskCardNumber(deposit.getCard().getCardNumber()));
        dto.setBankName(deposit.getCard().getBankName());
        dto.setAmount(deposit.getAmount());
        dto.setDescription(deposit.getDescription());
        dto.setTimestamp(deposit.getTimestamp());
        dto.setStatus(deposit.getStatus().name());
        return dto;
    }

    private CardDTO mapToDTO(Card card) {
        CardDTO dto = new CardDTO();
        dto.setId(card.getId());
        dto.setHolderName(card.getCardHolderName());
        dto.setBankName(card.getBankName());
        dto.setType(card.getType());
        dto.setExpiryDate(card.getExpiryDate());
        dto.setStatus(card.getStatus().name());
        dto.setBalanceCard(card.getBalanceCard());

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

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

    /**
     * Flexible user lookup - tries username first, then email
     */
    private User findUserByUsernameOrEmail(String identity) {
        System.out.println("CardService - Looking for user with identity: " + identity);
        
        // Try username first
        User user = userRepository.findByUserName(identity).orElse(null);
        if (user != null) {
            System.out.println("CardService - Found user by username: " + identity);
            return user;
        }
        
        // Try email
        user = userRepository.findByEmail(identity).orElse(null);
        if (user != null) {
            System.out.println("CardService - Found user by email: " + identity);
            return user;
        }
        
        System.out.println("CardService - User not found with identity: " + identity);
        throw new RuntimeException("User not found with identity: " + identity);
    }
}
