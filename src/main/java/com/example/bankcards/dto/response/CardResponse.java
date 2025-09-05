package com.example.bankcards.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CardResponse {
    private String maskedNumber;
    private String userFullName;
    private LocalDate expiryDate;
    private String status;
    private BigDecimal balance;
    private String currency;
    private boolean active;
    private boolean expired;

    public CardResponse(String maskedNumber, String userFullName,String currency,
                        LocalDate expiryDate, String status, BigDecimal balance, boolean active, boolean expired) {
        this.maskedNumber = maskedNumber;
        this.userFullName = userFullName;
        this.expiryDate = expiryDate;
        this.status = status;
        this.currency = currency;
        this.balance = balance;
        this.active = active;
        this.expired = expired;
    }
}