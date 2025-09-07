package com.example.bankcards.exception;

import com.example.bankcards.entity.enums.CardStatus;

public class ActiveCardsException extends RuntimeException{
    private CardStatus firstCardStatus;
    private CardStatus secondCardStatus;
    public ActiveCardsException(CardStatus firstCardStatus, CardStatus secondCardStatus) {
        super("Both cards must be active for transfer. From card status: " + firstCardStatus + " To card status: " + secondCardStatus);
        this.firstCardStatus = firstCardStatus;
        this.secondCardStatus = secondCardStatus;

    }
}
