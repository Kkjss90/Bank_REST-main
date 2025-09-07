package com.example.bankcards.exception;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class InsufficientFundsException extends RuntimeException {
    private final BigDecimal availableBalance;
    private final BigDecimal requestedAmount;

    public InsufficientFundsException(BigDecimal availableBalance, BigDecimal requestedAmount) {
        super("Insufficient funds. Available: " + availableBalance + ", Requested: " + requestedAmount);
        this.availableBalance = availableBalance;
        this.requestedAmount = requestedAmount;
    }

}