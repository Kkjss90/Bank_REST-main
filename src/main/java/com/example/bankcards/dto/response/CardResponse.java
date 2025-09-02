package com.example.bankcards.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CardResponse {
    private Long id;
    private String maskedNumber;
    private Long userId;
    private String userFullName;
    private LocalDate expiryDate;
    private String status;
    private BigDecimal balance;
    private String currency;
    private boolean active;
    private boolean expired;

    public CardResponse(Long id, String maskedNumber, Long userId, String userFullName,
                        LocalDate expiryDate, String status, BigDecimal balance, boolean active, boolean expired) {
        this.id = id;
        this.maskedNumber = maskedNumber;
        this.userId = userId;
        this.userFullName = userFullName;
        this.expiryDate = expiryDate;
        this.status = status;
        this.balance = balance;
        this.active = active;
        this.expired = expired;
    }
}