package com.example.bankcards.service;

import com.example.bankcards.dto.request.TransactionRequest;
import com.example.bankcards.dto.response.TransactionResponse;
import com.example.bankcards.entity.Transaction;
import java.util.List;

public interface TransactionService {
    List<Transaction> getAllTransactions();
    List<Transaction> getUserTransactions(Long userId);
    List<Transaction> getPendingTransactions();
    TransactionResponse transferBetweenCards(TransactionRequest transactionRequest);
    Transaction getTransactionById(Long id);
}