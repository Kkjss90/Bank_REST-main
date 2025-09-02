package com.example.bankcards.service.implementation;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.InsufficientFundsException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.CardNumberEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class CardServiceImpl implements CardService {
    
    private final CardRepository cardRepository;
    private final CardNumberEncryptor cardNumberEncryptor;

    @Autowired
    public CardServiceImpl(CardRepository cardRepository, CardNumberEncryptor cardNumberEncryptor) {
        this.cardRepository = cardRepository;
        this.cardNumberEncryptor = cardNumberEncryptor;
    }
    
    @Override
    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }
    
    @Override
    public Optional<Card> getCardById(Long id) {
        return cardRepository.findById(id);
    }
    
    @Override
    public Optional<Card> getCardByNumber(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber);
    }
    
    @Override
    public List<Card> getUserCards(Long userId) {
        return cardRepository.findByUserId(userId);
    }
    
    @Override
    public List<Card> getUserCards(User user) {
        return cardRepository.findByUser(user);
    }
    
    @Override
    public List<Card> getCardsByStatus(CardStatus status) {
        return cardRepository.findByStatus(status);
    }
    
    @Override
    public Card createCard(User user) {
        Card card = new Card();
        card.setUser(user);
        card.setCardNumber(cardNumberEncryptor.generateCardNumber());
        card.setMaskedNumber(cardNumberEncryptor.maskCardNumber(card.getCardNumber()));
        card.setExpiryDate(LocalDate.now().plusYears(3));
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.ZERO);
        
        return cardRepository.save(card);
    }
    
    @Override
    public Card blockCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        
        card.setStatus(CardStatus.BLOCKED);
        return cardRepository.save(card);
    }
    
    @Override
    public Card activateCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        
        card.setStatus(CardStatus.ACTIVE);
        return cardRepository.save(card);
    }
    
    @Override
    public Card depositToCard(Long cardId, BigDecimal amount) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        
        card.setBalance(card.getBalance().add(amount));
        return cardRepository.save(card);
    }

    @Override
    public Card withdrawFromCard(Long cardId, BigDecimal amount) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        if (card.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(card.getBalance(), amount);
        }

        card.setBalance(card.getBalance().subtract(amount));
        return cardRepository.save(card);
    }

    @Override
    public boolean cardExists(String cardNumber) {
        return cardRepository.existsByCardNumber(cardNumber);
    }

}