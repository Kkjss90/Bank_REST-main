package com.example.bankcards.service;

import com.example.bankcards.dto.request.TransactionRequest;
import com.example.bankcards.dto.response.TransactionResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.TransactionStatus;
import com.example.bankcards.mapper.Mapper;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.service.implementation.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CardService cardService;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private TransactionRequest transactionRequest;
    private Card fromCard;
    private Card toCard;
    private Transaction transaction;
    private TransactionResponse transactionResponse;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDateTime = LocalDateTime.now();

        transactionRequest = new TransactionRequest();
        transactionRequest.setFromCardId(1L);
        transactionRequest.setToCardId(2L);
        transactionRequest.setAmount(new BigDecimal("100.00"));
        transactionRequest.setDescription("Test transfer");

        fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setBalance(new BigDecimal("500.00"));
        fromCard.setStatus(CardStatus.ACTIVE);

        toCard = new Card();
        toCard.setId(2L);
        toCard.setBalance(new BigDecimal("200.00"));
        toCard.setStatus(CardStatus.ACTIVE);

        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setFromCard(fromCard);
        transaction.setToCard(toCard);
        transaction.setDescription("Test transfer");
        transaction.setStatus(TransactionStatus.PENDING);

        transactionResponse = new TransactionResponse(
                1L,
                new BigDecimal("100.00"),
                "1234567812345678",
                "8765432187654321",
                "Test transfer",
                "COMPLETED",
                testDateTime
        );
    }

    @Test
    void getAllTransactions_ShouldReturnAllTransactions() {
        List<Transaction> transactions = Arrays.asList(transaction);
        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> result = transactionService.getAllTransactions();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(transaction, result.get(0));
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    void getUserTransactions_ShouldReturnUserTransactions() {
        List<Transaction> transactions = Arrays.asList(transaction);
        when(transactionRepository.findByFromAccountUserIdOrToAccountUserId(1L, 1L))
                .thenReturn(transactions);

        List<Transaction> result = transactionService.getUserTransactions(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(transaction, result.get(0));
        verify(transactionRepository, times(1))
                .findByFromAccountUserIdOrToAccountUserId(1L, 1L);
    }

    @Test
    void getPendingTransactions_ShouldReturnPendingTransactions() {
        List<Transaction> transactions = Arrays.asList(transaction);
        when(transactionRepository.findByStatus(TransactionStatus.PENDING))
                .thenReturn(transactions);

        List<Transaction> result = transactionService.getPendingTransactions();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(TransactionStatus.PENDING, result.get(0).getStatus());
        verify(transactionRepository, times(1))
                .findByStatus(TransactionStatus.PENDING);
    }

    @Test
    void transferBetweenCards_ShouldCompleteSuccessfully() {
        when(cardService.getCardById(1L)).thenReturn(Optional.of(fromCard));
        when(cardService.getCardById(2L)).thenReturn(Optional.of(toCard));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(cardService.withdrawFromCard(anyLong(), any(BigDecimal.class))).thenReturn(fromCard);
        when(cardService.depositToCard(anyLong(), any(BigDecimal.class))).thenReturn(toCard);
        when(mapper.dtoToResponse(any(Transaction.class))).thenReturn(transactionResponse);

        TransactionResponse result = transactionService.transferBetweenCards(transactionRequest);

        assertNotNull(result);
        assertEquals("COMPLETED", result.getStatus());

        verify(cardService, times(1)).getCardById(1L);
        verify(cardService, times(1)).getCardById(2L);
        verify(transactionRepository, times(2)).save(any(Transaction.class));
        verify(cardService, times(1)).withdrawFromCard(1L, new BigDecimal("100.00"));
        verify(cardService, times(1)).depositToCard(2L, new BigDecimal("100.00"));
        verify(mapper, times(1)).dtoToResponse(any(Transaction.class));
    }

    @Test
    void transferBetweenCards_WhenFromCardNotFound_ShouldThrowException() {
        when(cardService.getCardById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> transactionService.transferBetweenCards(transactionRequest));

        verify(cardService, times(1)).getCardById(1L);
        verify(cardService, never()).getCardById(2L);
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void transferBetweenCards_WhenToCardNotFound_ShouldThrowException() {
        when(cardService.getCardById(1L)).thenReturn(Optional.of(fromCard));
        when(cardService.getCardById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> transactionService.transferBetweenCards(transactionRequest));

        verify(cardService, times(1)).getCardById(1L);
        verify(cardService, times(1)).getCardById(2L);
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void transferBetweenCards_WhenInsufficientFunds_ShouldThrowException() {
        fromCard.setBalance(new BigDecimal("50.00"));
        when(cardService.getCardById(1L)).thenReturn(Optional.of(fromCard));
        when(cardService.getCardById(2L)).thenReturn(Optional.of(toCard));

        assertThrows(RuntimeException.class,
                () -> transactionService.transferBetweenCards(transactionRequest));

        verify(transactionRepository, never()).save(any());
        verify(cardService, never()).withdrawFromCard(anyLong(), any());
    }

    @Test
    void transferBetweenCards_WhenFromCardNotActive_ShouldThrowException() {
        fromCard.setStatus(CardStatus.BLOCKED);
        when(cardService.getCardById(1L)).thenReturn(Optional.of(fromCard));
        when(cardService.getCardById(2L)).thenReturn(Optional.of(toCard));

        assertThrows(RuntimeException.class,
                () -> transactionService.transferBetweenCards(transactionRequest));

        verify(transactionRepository, never()).save(any());
        verify(cardService, never()).withdrawFromCard(anyLong(), any());
    }

    @Test
    void transferBetweenCards_WhenToCardNotActive_ShouldThrowException() {
        toCard.setStatus(CardStatus.BLOCKED);
        when(cardService.getCardById(1L)).thenReturn(Optional.of(fromCard));
        when(cardService.getCardById(2L)).thenReturn(Optional.of(toCard));

        assertThrows(RuntimeException.class,
                () -> transactionService.transferBetweenCards(transactionRequest));

        verify(transactionRepository, never()).save(any());
        verify(cardService, never()).depositToCard(anyLong(), any());
    }

    @Test
    void transferBetweenCards_WhenTransferFails_ShouldMarkAsFailed() {
        when(cardService.getCardById(1L)).thenReturn(Optional.of(fromCard));
        when(cardService.getCardById(2L)).thenReturn(Optional.of(toCard));
        doThrow(new RuntimeException("Bank error"))
                .when(cardService).withdrawFromCard(1L, new BigDecimal("100.00"));

        Transaction failedTransaction = new Transaction();
        failedTransaction.setStatus(TransactionStatus.FAILED);
        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(transaction)
                .thenReturn(failedTransaction);

        assertThrows(RuntimeException.class,
                () -> transactionService.transferBetweenCards(transactionRequest));

        verify(transactionRepository, times(2)).save(any(Transaction.class));
        verify(cardService, times(1)).withdrawFromCard(1L, new BigDecimal("100.00"));
        verify(cardService, never()).depositToCard(anyLong(), any());
        verify(mapper, never()).dtoToResponse(any(Transaction.class));
    }

    @Test
    void getTransactionById_WhenTransactionExists_ShouldReturnTransaction() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        Transaction result = transactionService.getTransactionById(1L);

        assertNotNull(result);
        assertEquals(transaction, result);
        verify(transactionRepository, times(1)).findById(1L);
    }

    @Test
    void getTransactionById_WhenTransactionNotExists_ShouldThrowException() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> transactionService.getTransactionById(1L));

        verify(transactionRepository, times(1)).findById(1L);
    }

    @Test
    void transferBetweenCards_ShouldSetCorrectTransactionDetails() {
        when(cardService.getCardById(1L)).thenReturn(Optional.of(fromCard));
        when(cardService.getCardById(2L)).thenReturn(Optional.of(toCard));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction savedTransaction = invocation.getArgument(0);
            savedTransaction.setId(1L);
            return savedTransaction;
        });
        when(cardService.withdrawFromCard(anyLong(), any(BigDecimal.class))).thenReturn(fromCard);
        when(cardService.depositToCard(anyLong(), any(BigDecimal.class))).thenReturn(toCard);
        when(mapper.dtoToResponse(any(Transaction.class))).thenReturn(transactionResponse);

        transactionService.transferBetweenCards(transactionRequest);

        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }
}
