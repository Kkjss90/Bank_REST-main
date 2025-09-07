package com.example.bankcards.service;

import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.InsufficientFundsException;
import com.example.bankcards.mapper.Mapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.implementation.CardServiceImpl;
import com.example.bankcards.util.CardNumberEncryptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.bankcards.entity.enums.CardStatus.ACTIVE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardNumberEncryptor cardNumberEncryptor;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private CardServiceImpl cardService;

    private User user;
    private Card card;
    private CardResponse cardResponse;
    private Card blockedCard;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        card = new Card();
        card.setId(1L);
        card.setCardNumber("1234567812345678");
        card.setMaskedNumber("********5678");
        card.setCurrency("USD");
        card.setBalance(new BigDecimal("500.00"));
        card.setStatus(ACTIVE);
        card.setActive(true);
        card.setExpiryDate(LocalDate.now().plusYears(3));
        card.setUser(user);

        blockedCard = new Card();
        blockedCard.setId(2L);
        blockedCard.setCardNumber("8765432187654321");
        blockedCard.setMaskedNumber("********4321");
        blockedCard.setCurrency("EUR");
        blockedCard.setBalance(new BigDecimal("200.00"));
        blockedCard.setStatus(CardStatus.BLOCKED);
        blockedCard.setActive(false);
        blockedCard.setExpiryDate(LocalDate.now().plusYears(2));
        blockedCard.setUser(user);

        cardResponse = new CardResponse(
                "********5678",
                "testuser",
                "USD",
                LocalDate.now().plusYears(3),
                "ACTIVE",
                new BigDecimal("500.00"),
                true,
                false
        );
    }

    @Test
    void getAllCards_ShouldReturnAllCards() {
        List<Card> cards = Arrays.asList(card, blockedCard);
        when(cardRepository.findAll()).thenReturn(cards);
        when(mapper.dtoToResponse(card)).thenReturn(cardResponse);
        when(mapper.dtoToResponse(blockedCard)).thenReturn(new CardResponse(
                "********5678",
                "testuser",
                "USD",
                LocalDate.now().plusYears(3),
                "BLOCKED",
                new BigDecimal("500.00"),
                true,
                false));

        List<CardResponse> result = cardService.getAllCards();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(cardRepository, times(1)).findAll();
        verify(mapper, times(2)).dtoToResponse(any(Card.class));
    }

    @Test
    void getCardById_WhenCardExists_ShouldReturnCard() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        Optional<Card> result = cardService.getCardById(1L);

        assertTrue(result.isPresent());
        assertEquals(card, result.get());
        verify(cardRepository, times(1)).findById(1L);
    }

    @Test
    void getCardById_WhenCardNotExists_ShouldReturnEmpty() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Card> result = cardService.getCardById(1L);

        assertFalse(result.isPresent());
        verify(cardRepository, times(1)).findById(1L);
    }

    @Test
    void getCardByNumber_WhenCardExists_ShouldReturnCard() {
        when(cardRepository.findByCardNumber("1234567812345678")).thenReturn(Optional.of(card));

        Optional<Card> result = cardService.getCardByNumber("1234567812345678");

        assertTrue(result.isPresent());
        assertEquals(card, result.get());
        verify(cardRepository, times(1)).findByCardNumber("1234567812345678");
    }

    @Test
    void getUserCards_ByUserId_ShouldReturnUserCards() {
        List<Card> cards = Arrays.asList(card, blockedCard);
        when(cardRepository.findByUserId(1L)).thenReturn(cards);

        List<Card> result = cardService.getUserCards(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(cardRepository, times(1)).findByUserId(1L);
    }

    @Test
    void getUserCards_ByUser_ShouldReturnCardResponses() {
        List<Card> cards = Arrays.asList(card, blockedCard);
        when(cardRepository.findByUserId(1L)).thenReturn(cards);
        when(mapper.dtoToResponse(card)).thenReturn(cardResponse);
        when(mapper.dtoToResponse(blockedCard)).thenReturn(new CardResponse(
                "********5678",
                "testuser",
                "USD",
                LocalDate.now().plusYears(3),
                "BLOCKED",
                new BigDecimal("500.00"),
                true,
                false
        ));

        List<CardResponse> result = cardService.getUserCards(user);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(cardRepository, times(1)).findByUserId(1L);
        verify(mapper, times(2)).dtoToResponse(any(Card.class));
    }

    @Test
    void getUserCardsPaginated_WithoutSearch_ShouldReturnPaginatedCards() {
        Page<Card> cardPage = new PageImpl<>(Arrays.asList(card));
        Pageable pageable = Pageable.ofSize(10);
        when(cardRepository.findByUser(user, pageable)).thenReturn(cardPage);
        when(mapper.dtoToResponse(card)).thenReturn(cardResponse);

        Page<CardResponse> result = cardService.getUserCardsPaginated(user, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(cardRepository, times(1)).findByUser(user, pageable);
        verify(mapper, times(1)).dtoToResponse(card);
    }

    @Test
    void getUserCardsPaginated_WithSearch_ShouldReturnFilteredCards() {
        Page<Card> cardPage = new PageImpl<>(Arrays.asList(card));
        Pageable pageable = Pageable.ofSize(10);
        when(cardRepository.findByCardNumber("1234", pageable)).thenReturn(cardPage);
        when(mapper.dtoToResponse(card)).thenReturn(cardResponse);

        Page<CardResponse> result = cardService.getUserCardsPaginated(user, "1234", pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(cardRepository, times(1)).findByCardNumber("1234", pageable);
        verify(mapper, times(1)).dtoToResponse(card);
    }

    @Test
    void getCardsByStatus_ShouldReturnFilteredCards() {
        List<Card> activeCards = Arrays.asList(card);
        when(cardRepository.findByStatus(ACTIVE)).thenReturn(activeCards);

        List<Card> result = cardService.getCardsByStatus(ACTIVE);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ACTIVE, result.get(0).getStatus());
        verify(cardRepository, times(1)).findByStatus(ACTIVE);
    }

    @Test
    void getCardsByUserAndStatus_ShouldReturnFilteredCardResponses() {
        List<Card> userCards = Arrays.asList(card, blockedCard);
        when(cardRepository.findByUser(user)).thenReturn(userCards);
        when(mapper.dtoToResponse(card)).thenReturn(cardResponse);

        List<CardResponse> result = cardService.getCardsByUserAndStatus(user, ACTIVE);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(cardRepository, times(1)).findByUser(user);
        verify(mapper, times(1)).dtoToResponse(card);
    }

    @Test
    void createCard_ShouldCreateNewCard() {
        when(cardNumberEncryptor.generateCardNumber()).thenReturn("1234567812345678");
        when(cardNumberEncryptor.maskCardNumber("1234567812345678")).thenReturn("********5678");
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        when(mapper.dtoToResponse(any(Card.class))).thenReturn(cardResponse);

        CardResponse result = cardService.createCard(user, "USD");

        assertNotNull(result);
        verify(cardNumberEncryptor, times(1)).generateCardNumber();
        verify(cardNumberEncryptor, times(1)).maskCardNumber("1234567812345678");
        verify(cardRepository, times(1)).save(any(Card.class));
        verify(mapper, times(1)).dtoToResponse(any(Card.class));
    }


    @Test
    void deleteCard_ShouldCallRepositoryDelete() {
        cardService.deleteCard(1L);

        verify(cardRepository, times(1)).deleteById(1L);
    }

    @Test
    void blockCard_WhenCardExists_ShouldBlockCard() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);

        cardService.blockCard(1L);

        assertEquals(CardStatus.BLOCKED, card.getStatus());
        assertFalse(card.isActive());
        verify(cardRepository, times(1)).findById(1L);
        verify(cardRepository, times(1)).save(card);
    }

    @Test
    void blockCard_WhenCardNotExists_ShouldThrowException() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> cardService.blockCard(1L));
        verify(cardRepository, times(1)).findById(1L);
        verify(cardRepository, never()).save(any());
    }

    @Test
    void activateCard_WhenCardExists_ShouldActivateCard() {
        blockedCard.setActive(false);
        when(cardRepository.findById(2L)).thenReturn(Optional.of(blockedCard));
        when(cardRepository.save(blockedCard)).thenReturn(blockedCard);

        cardService.activateCard(2L);

        assertEquals(ACTIVE, blockedCard.getStatus());
        assertTrue(blockedCard.isActive());
        verify(cardRepository, times(1)).findById(2L);
        verify(cardRepository, times(1)).save(blockedCard);
    }

    @Test
    void activateCard_WhenCardNotExists_ShouldThrowException() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> cardService.activateCard(1L));
        verify(cardRepository, times(1)).findById(1L);
        verify(cardRepository, never()).save(any());
    }

    @Test
    void depositToCard_WhenCardExists_ShouldIncreaseBalance() {
        BigDecimal initialBalance = card.getBalance();
        BigDecimal depositAmount = new BigDecimal("100.00");
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);

        Card result = cardService.depositToCard(1L, depositAmount);

        assertNotNull(result);
        assertEquals(initialBalance.add(depositAmount), result.getBalance());
        verify(cardRepository, times(1)).findById(1L);
        verify(cardRepository, times(1)).save(card);
    }

    @Test
    void depositToCard_WhenCardNotExists_ShouldThrowException() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                cardService.depositToCard(1L, new BigDecimal("100.00")));
        verify(cardRepository, times(1)).findById(1L);
        verify(cardRepository, never()).save(any());
    }

    @Test
    void withdrawFromCard_WhenSufficientFunds_ShouldDecreaseBalance() {
        BigDecimal initialBalance = card.getBalance();
        BigDecimal withdrawAmount = new BigDecimal("100.00");
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);

        Card result = cardService.withdrawFromCard(1L, withdrawAmount);

        assertNotNull(result);
        assertEquals(initialBalance.subtract(withdrawAmount), result.getBalance());
        verify(cardRepository, times(1)).findById(1L);
        verify(cardRepository, times(1)).save(card);
    }

    @Test
    void withdrawFromCard_WhenInsufficientFunds_ShouldThrowException() {
        BigDecimal withdrawAmount = new BigDecimal("600.00"); // Больше чем баланс
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        assertThrows(InsufficientFundsException.class, () ->
                cardService.withdrawFromCard(1L, withdrawAmount));
        verify(cardRepository, times(1)).findById(1L);
        verify(cardRepository, never()).save(any());
    }

    @Test
    void withdrawFromCard_WhenCardNotExists_ShouldThrowException() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                cardService.withdrawFromCard(1L, new BigDecimal("100.00")));
        verify(cardRepository, times(1)).findById(1L);
        verify(cardRepository, never()).save(any());
    }

    @Test
    void cardExists_WhenCardExists_ShouldReturnTrue() {
        when(cardRepository.existsById(1L)).thenReturn(true);

        boolean result = cardService.cardExists(1L);

        assertTrue(result);
        verify(cardRepository, times(1)).existsById(1L);
    }

    @Test
    void cardExists_WhenCardNotExists_ShouldReturnFalse() {
        when(cardRepository.existsById(1L)).thenReturn(false);

        boolean result = cardService.cardExists(1L);

        assertFalse(result);
        verify(cardRepository, times(1)).existsById(1L);
    }

    @Test
    void createCard_ShouldSetCorrectProperties() {
        when(cardNumberEncryptor.generateCardNumber()).thenReturn("1234567812345678");
        when(cardNumberEncryptor.maskCardNumber("1234567812345678")).thenReturn("1234****5678");
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> {
            Card savedCard = invocation.getArgument(0);
            savedCard.setId(1L); // Симулируем сохранение с ID
            return savedCard;
        });
        when(mapper.dtoToResponse(any(Card.class))).thenReturn(cardResponse);

        CardResponse result = cardService.createCard(user, "USD");

        assertNotNull(result);
        verify(cardRepository, times(1)).save(any(Card.class));
    }
}