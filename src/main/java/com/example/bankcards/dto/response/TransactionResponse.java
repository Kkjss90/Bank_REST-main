package com.example.bankcards.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * The type Transaction response.
 */
@Data
public class TransactionResponse {
    private Long id;
    private BigDecimal amount;
    private String fromCardNumber;
    private String toCardNumber;
    private String description;
    private String status;
    private LocalDateTime createdAt;

    /**
     * Instantiates a new Transaction response.
     *
     * @param id             the id
     * @param amount         the amount
     * @param fromCardNumber the from card number
     * @param toCardNumber   the to card number
     * @param description    the description
     * @param status         the status
     * @param createdAt      the created at
     */
    public TransactionResponse(Long id, BigDecimal amount,
                              String fromCardNumber,
                              String toCardNumber, String description,
                              String status, LocalDateTime createdAt) {
        this.id = id;
        this.amount = amount;
        this.fromCardNumber = fromCardNumber;
        this.toCardNumber = toCardNumber;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }
}