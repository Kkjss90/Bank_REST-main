package com.example.bankcards.repository;

import com.example.bankcards.entity.Token;
import com.example.bankcards.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The interface Token repository.
 */
@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    /**
     * Find by token token.
     *
     * @param token the token
     * @return the token
     */
    Token findByToken(String token);

    /**
     * Find by user token.
     *
     * @param user the user
     * @return the token
     */
    Token findByUser(User user);

    /**
     * Delete by token.
     *
     * @param token the token
     */
    void deleteByToken(String token);
}