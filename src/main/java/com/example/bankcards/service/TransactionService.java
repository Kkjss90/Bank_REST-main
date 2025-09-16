package com.example.bankcards.service;

import com.example.bankcards.dto.request.TransactionRequest;
import com.example.bankcards.dto.response.TransactionResponse;
import com.example.bankcards.entity.Transaction;
import java.util.List;

/**
 * The interface Transaction service.
 */
public interface TransactionService {
    /**
     * Gets all transactions.
     *
     * @return the all transactions
     */
    List<Transaction> getAllTransactions();

    /**
     * Gets user transactions.
     *
     * @param userId the user id
     * @return the user transactions
     */
    List<Transaction> getUserTransactions(Long userId);

    /**
     * Gets pending transactions.
     *
     * @return the pending transactions
     */
    List<Transaction> getPendingTransactions();

    /**
     * Transfer between cards transaction response.
     *
     * @param transactionRequest the transaction request
     * @return the transaction response
     */
    TransactionResponse transferBetweenCards(TransactionRequest transactionRequest);

    /**
     * Gets transaction by id.
     *
     * @param id the id
     * @return the transaction by id
     */
    Transaction getTransactionById(Long id);
}