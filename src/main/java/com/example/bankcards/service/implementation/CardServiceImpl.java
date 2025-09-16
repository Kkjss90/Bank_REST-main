package com.example.bankcards.service.implementation;

import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardException;
import com.example.bankcards.exception.InsufficientFundsException;
import com.example.bankcards.mapper.Mapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.ApiMessages;
import com.example.bankcards.util.CardNumberEncryptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The type Card service.
 */
@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CardServiceImpl implements CardService {
    
    private final CardRepository cardRepository;
    private final CardNumberEncryptor cardNumberEncryptor;
    private  final Mapper mapper;

    /**
     * Gets all cards.
     *
     * @return the all cards
     */
    @Override
    public List<CardResponse> getAllCards() {
        List<Card> cards = cardRepository.findAll();
        List<CardResponse> cardResponses = cards.stream()
                .map(mapper::dtoToResponse)
                .collect(Collectors.toList());;
        return cardResponses;
    }

    /**
     * Gets card by id.
     *
     * @param id the id
     * @return the card by id
     */
    @Override
    public Optional<Card> getCardById(Long id) {
        return cardRepository.findById(id);
    }

    /**
     * Gets card by number.
     *
     * @param cardNumber the card number
     * @return the card by number
     */
    @Override
    public Optional<Card> getCardByNumber(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber);
    }

    /**
     * Gets user cards.
     *
     * @param userId the user id
     * @return the user cards
     */
    @Override
    public List<Card> getUserCards(Long userId) {
        return cardRepository.findByUserId(userId);
    }

    /**
     * Gets user cards.
     *
     * @param user the user
     * @return the user cards
     */
    @Override
    public List<CardResponse> getUserCards(User user) {
        List<Card> cards = cardRepository.findByUserId(user.getId());
        List<CardResponse> cardResponses = cards.stream()
                .map(mapper::dtoToResponse)
                .collect(Collectors.toList());
        return cardResponses;
    }

    /**
     * Gets user cards paginated.
     *
     * @param user     the user
     * @param search   the search
     * @param pageable the pageable
     * @return the user cards paginated
     */
    @Override
    public Page<CardResponse> getUserCardsPaginated(User user, String search, Pageable pageable) {
        Page<Card> cardsPage;
        if (search == null || search.isEmpty()) {
            cardsPage = cardRepository.findByUser(user, pageable);
        }
        else {
            cardsPage = cardRepository.findByCardNumber(search, pageable);
        }
        return cardsPage.map(mapper::dtoToResponse);
    }

    /**
     * Gets cards by status.
     *
     * @param status the status
     * @return the cards by status
     */
    @Override
    public List<Card> getCardsByStatus(CardStatus status) {
        return cardRepository.findByStatus(status);
    }

    /**
     * Gets cards by user and status.
     *
     * @param user   the user
     * @param status the status
     * @return the cards by user and status
     */
    @Override
    public List<CardResponse> getCardsByUserAndStatus(User user, CardStatus status) {
        List<Card> cards = cardRepository.findByUser(user);
        List<CardResponse> filteredCards = new ArrayList<>();
        for (Card card : cards) {
            if (card.getStatus().equals(status)) {
                filteredCards.add(mapper.dtoToResponse(card));
            }
        }
        return filteredCards;
    }

    /**
     * Create card.
     *
     * @param user     the user
     * @param currency the currency
     * @return the card response
     */
    @Override
    public CardResponse createCard(User user, String currency) {
        Card card = new Card();
        card.setUser(user);
        card.setCardNumber(cardNumberEncryptor.generateCardNumber());
        card.setMaskedNumber(cardNumberEncryptor.maskCardNumber(card.getCardNumber()));
        card.setCurrency(currency);
        card.setExpiryDate(LocalDate.now().plusYears(3));
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.ZERO);
        cardRepository.save(card);
        return mapper.dtoToResponse(card);
    }

    /**
     * Delete card.
     *
     * @param cardId the card id
     */
    @Override
    @Transactional
    public void deleteCard(Long cardId) {
        cardRepository.deleteById(cardId);
    }

    /**
     * Block card.
     *
     * @param cardId the card id
     */
    @Override
    @Transactional
    public void blockCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardException(ApiMessages.CARD_NOT_FOUND.getMessage()));
        
        card.setStatus(CardStatus.BLOCKED);
        card.setActive(false);
        cardRepository.save(card);
    }

    /**
     * Activate card.
     *
     * @param cardId the card id
     */
    @Override
    @Transactional
    public void activateCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardException(ApiMessages.CARD_NOT_FOUND.getMessage()));
        
        card.setStatus(CardStatus.ACTIVE);
        card.setActive(true);
        cardRepository.save(card);
    }

    /**
     * Deposit to card.
     *
     * @param cardId the card id
     * @param amount the amount
     * @return the card
     */
    @Override
    @Transactional
    public Card depositToCard(Long cardId, BigDecimal amount) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardException(ApiMessages.CARD_NOT_FOUND.getMessage()));
        
        card.setBalance(card.getBalance().add(amount));
        return cardRepository.save(card);
    }

    /**
     * Withdraw from card.
     *
     * @param cardId the card id
     * @param amount the amount
     * @return the card
     */
    @Override
    @Transactional
    public Card withdrawFromCard(Long cardId, BigDecimal amount) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardException(ApiMessages.CARD_NOT_FOUND.getMessage()));

        if (card.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(card.getBalance(), amount);
        }

        card.setBalance(card.getBalance().subtract(amount));
        return cardRepository.save(card);
    }

    /**
     * Card exists boolean.
     *
     * @param cardId the card id
     * @return the boolean
     */
    @Override
    public boolean cardExists(Long cardId) {
        return cardRepository.existsById(cardId);
    }

}