package com.example.bankcards.service.implementation;

import com.example.bankcards.dto.request.TransactionRequest;
import com.example.bankcards.dto.response.TransactionResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.TransactionStatus;
import com.example.bankcards.exception.ActiveCardsException;
import com.example.bankcards.exception.InsufficientFundsException;
import com.example.bankcards.exception.TransferException;
import com.example.bankcards.mapper.Mapper;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.TransactionService;
import com.example.bankcards.util.ApiMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * The type Transaction service.
 */
@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CardService cardService;
    private final Mapper mapper;

    /**
     * Gets all transactions.
     *
     * @return the all transactions
     */
    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    /**
     * Gets user transactions.
     *
     * @param userId the user id
     * @return the user transactions
     */
    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getUserTransactions(Long userId) {
        return transactionRepository.findByFromAccountUserIdOrToAccountUserId(userId, userId);
    }

    /**
     * Gets pending transactions.
     *
     * @return the pending transactions
     */
    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getPendingTransactions() {
        return transactionRepository.findByStatus(TransactionStatus.PENDING);
    }

    /**
     * Transfer between cards transaction response.
     *
     * @param transactionRequest the transaction request
     * @return the transaction response
     */
    @Override
    @Transactional
    public TransactionResponse transferBetweenCards(TransactionRequest transactionRequest) {
        Card fromCard = cardService.getCardById(transactionRequest.getFromCardId())
                .orElseThrow(() -> new TransferException(ApiMessages.SOURCE_ACCOUNT_NOT_FOUND.getMessage()));

        Card toCard = cardService.getCardById(transactionRequest.getToCardId())
                .orElseThrow(() -> new TransferException(ApiMessages.DESTINATION_ACCOUNT_NOT_FOUND.getMessage()));


        if (fromCard.getId().equals(toCard.getId())) {
            throw new TransferException("Cannot transfer to the same card");
        }

        if (fromCard.getBalance().compareTo(transactionRequest.getAmount()) < 0) {
            throw new InsufficientFundsException(fromCard.getBalance(), transactionRequest.getAmount());
        }
        if (fromCard.getStatus() != CardStatus.ACTIVE || toCard.getStatus() != CardStatus.ACTIVE) {
            throw new ActiveCardsException(fromCard.getStatus(), toCard.getStatus());
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setFromCard(fromCard);
        transaction.setToCard(toCard);
        transaction.setDescription(transactionRequest.getDescription());
        transaction.setStatus(TransactionStatus.PENDING);

        Transaction savedTransaction = transactionRepository.save(transaction);

        try {
            cardService.withdrawFromCard(transactionRequest.getFromCardId(), transactionRequest.getAmount());
            cardService.depositToCard(transactionRequest.getToCardId(), transactionRequest.getAmount());

            savedTransaction.setStatus(TransactionStatus.COMPLETED);
            Transaction transactionResp = transactionRepository.save(savedTransaction);
            return mapper.dtoToResponse(transactionResp);

        } catch (Exception e) {
            savedTransaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(savedTransaction);
            throw new TransferException(ApiMessages.TRANSACTION_FAILED.getMessage());
        }
    }

    /**
     * Gets transaction by id.
     *
     * @param id the id
     * @return the transaction by id
     */
    @Override
    @Transactional(readOnly = true)
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new TransferException(ApiMessages.TRANSACTION_NOT_FOUND.getMessage()));
    }
}