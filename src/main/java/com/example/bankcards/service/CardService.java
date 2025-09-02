package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.User;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CardService {
    List<Card> getAllCards();
    Optional<Card> getCardById(Long id);
    Optional<Card> getCardByNumber(String cardNumber);
    List<Card> getUserCards(Long userId);
    List<Card> getUserCards(User user);
    List<Card> getCardsByStatus(CardStatus status);
    Card createCard(User user);
    Card blockCard(Long cardId);
    Card activateCard(Long cardId);
    Card depositToCard(Long cardId, BigDecimal amount);
    Card withdrawFromCard(Long cardId, BigDecimal amount);
    boolean cardExists(String cardNumber);
}