package com.example.bankcards.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransactionRequest {
    @NotNull
    private Long fromCardId;
    
    @NotNull
    private Long toCardId;
    
    @NotNull
    @Positive
    private BigDecimal amount;
    
    private String description;
}