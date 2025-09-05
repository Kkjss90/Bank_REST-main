package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.CardStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "card")
@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String cardNumber;

    private String maskedNumber;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String currency;
    private LocalDate expiryDate;
    private boolean isExpired;
    private boolean isActive = true;

    @Enumerated(EnumType.STRING)
    private CardStatus status = CardStatus.ACTIVE;

    private BigDecimal balance = BigDecimal.ZERO;

}
