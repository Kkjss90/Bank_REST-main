package com.example.bankcards.exception;

import com.example.bankcards.entity.enums.CardStatus;
import lombok.Getter;
import lombok.Setter;

/**
 * The type Active cards exception.
 */
@Getter
@Setter
public class ActiveCardsException extends RuntimeException{
    private CardStatus firstCardStatus;
    private CardStatus secondCardStatus;

    /**
     * Instantiates a new Active cards exception.
     *
     * @param firstCardStatus  the first card status
     * @param secondCardStatus the second card status
     */
    public ActiveCardsException(CardStatus firstCardStatus, CardStatus secondCardStatus) {
        super("Both cards must be active for transfer. From card status: " + firstCardStatus + " To card status: " + secondCardStatus);
        this.firstCardStatus = firstCardStatus;
        this.secondCardStatus = secondCardStatus;

    }
}
