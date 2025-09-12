package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.CardStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "card")
@Getter
@Setter
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Card card = (Card) o;

        if (cardNumber != null && card.cardNumber != null) {
            return Objects.equals(cardNumber, card.cardNumber);
        }

        return false;
    }

    @Override
    public int hashCode() {
        if (cardNumber != null) {
            return cardNumber.hashCode();
        }
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", cardNumber='" + cardNumber + '\'' +
                ", maskedNumber='" + maskedNumber + '\'' +
                ", userId=" + (user != null ? user.getId() : "null") + // Только ID пользователя во избежание циклических ссылок
                ", currency='" + currency + '\'' +
                ", expiryDate=" + expiryDate +
                ", isExpired=" + isExpired +
                ", isActive=" + isActive +
                ", status=" + status +
                ", balance=" + balance +
                '}';
    }

}
