package com.example.bankcards.service;

import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.User;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CardService {
    List<CardResponse> getAllCards();
    Optional<Card> getCardById(Long id);
    Optional<Card> getCardByNumber(String cardNumber);
    List<Card> getUserCards(Long userId);
    List<CardResponse> getUserCards(User user);
    List<Card> getCardsByStatus(CardStatus status);
    List<CardResponse> getCardsByUserAndStatus(User user, CardStatus status);
    CardResponse createCard(User user, String currency);
    void deleteCard(Long cardId);
    void blockCard(Long cardId);
    void activateCard(Long cardId);
    Card depositToCard(Long cardId, BigDecimal amount);
    Card withdrawFromCard(Long cardId, BigDecimal amount);
    boolean cardExists(Long cardId);
}