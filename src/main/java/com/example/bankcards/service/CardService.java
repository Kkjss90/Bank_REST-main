package com.example.bankcards.service;

import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * The interface Card service.
 */
public interface CardService {
    /**
     * Gets all cards.
     *
     * @return the all cards
     */
    List<CardResponse> getAllCards();

    /**
     * Gets card by id.
     *
     * @param id the id
     * @return the card by id
     */
    Optional<Card> getCardById(Long id);

    /**
     * Gets card by number.
     *
     * @param cardNumber the card number
     * @return the card by number
     */
    Optional<Card> getCardByNumber(String cardNumber);

    /**
     * Gets user cards.
     *
     * @param userId the user id
     * @return the user cards
     */
    List<Card> getUserCards(Long userId);

    /**
     * Gets user cards.
     *
     * @param user the user
     * @return the user cards
     */
    List<CardResponse> getUserCards(User user);

    /**
     * Gets user cards paginated.
     *
     * @param user     the user
     * @param search   the search
     * @param pageable the pageable
     * @return the user cards paginated
     */
    Page<CardResponse> getUserCardsPaginated(User user, String search, Pageable pageable);

    /**
     * Gets cards by status.
     *
     * @param status the status
     * @return the cards by status
     */
    List<Card> getCardsByStatus(CardStatus status);

    /**
     * Gets cards by user and status.
     *
     * @param user   the user
     * @param status the status
     * @return the cards by user and status
     */
    List<CardResponse> getCardsByUserAndStatus(User user, CardStatus status);

    /**
     * Create card card response.
     *
     * @param user     the user
     * @param currency the currency
     * @return the card response
     */
    CardResponse createCard(User user, String currency);

    /**
     * Delete card.
     *
     * @param cardId the card id
     */
    void deleteCard(Long cardId);

    /**
     * Block card.
     *
     * @param cardId the card id
     */
    void blockCard(Long cardId);

    /**
     * Activate card.
     *
     * @param cardId the card id
     */
    void activateCard(Long cardId);

    /**
     * Deposit to card card.
     *
     * @param cardId the card id
     * @param amount the amount
     * @return the card
     */
    Card depositToCard(Long cardId, BigDecimal amount);

    /**
     * Withdraw from card card.
     *
     * @param cardId the card id
     * @param amount the amount
     * @return the card
     */
    Card withdrawFromCard(Long cardId, BigDecimal amount);

    /**
     * Card exists boolean.
     *
     * @param cardId the card id
     * @return the boolean
     */
    boolean cardExists(Long cardId);
}