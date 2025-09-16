package com.example.bankcards.exception;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * The type Insufficient funds exception.
 */
@Getter
public class InsufficientFundsException extends RuntimeException {
    private final BigDecimal availableBalance;
    private final BigDecimal requestedAmount;

    /**
     * Instantiates a new Insufficient funds exception.
     *
     * @param availableBalance the available balance
     * @param requestedAmount  the requested amount
     */
    public InsufficientFundsException(BigDecimal availableBalance, BigDecimal requestedAmount) {
        super("Insufficient funds. Available: " + availableBalance + ", Requested: " + requestedAmount);
        this.availableBalance = availableBalance;
        this.requestedAmount = requestedAmount;
    }

}