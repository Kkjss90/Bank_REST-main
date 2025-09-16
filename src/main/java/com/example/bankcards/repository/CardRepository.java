package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * The interface Card repository.
 */
@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    /**
     * Find by card number optional.
     *
     * @param cardNumber the card number
     * @return the optional
     */
    Optional<Card> findByCardNumber(String cardNumber);

    /**
     * Find by user list.
     *
     * @param user the user
     * @return the list
     */
    List<Card> findByUser(User user);

    /**
     * Find by user id list.
     *
     * @param userId the user id
     * @return the list
     */
    List<Card> findByUserId(Long userId);

    /**
     * Find by status list.
     *
     * @param status the status
     * @return the list
     */
    List<Card> findByStatus(CardStatus status);

    /**
     * Find by user page.
     *
     * @param user     the user
     * @param pageable the pageable
     * @return the page
     */
    Page<Card> findByUser(User user, Pageable pageable);

    /**
     * Find by card number page.
     *
     * @param cardNumber the card number
     * @param pageable   the pageable
     * @return the page
     */
    Page<Card> findByCardNumber(String cardNumber, Pageable pageable);

    /**
     * Exists by id boolean.
     *
     * @param cardId the card id
     * @return the boolean
     */
    boolean existsById(Long cardId);
}