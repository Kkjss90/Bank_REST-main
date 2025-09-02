package com.example.bankcards.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {
    private Long id;
    private BigDecimal amount;
    private String fromCardNumber;
    private String toCardNumber;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    
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