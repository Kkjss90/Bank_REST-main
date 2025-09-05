package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.TransactionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Positive
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "from_card_id")
    private Card fromCard;

    @ManyToOne
    @JoinColumn(name = "to_card_id")
    private Card toCard;

    private String description;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status = TransactionStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();
}
