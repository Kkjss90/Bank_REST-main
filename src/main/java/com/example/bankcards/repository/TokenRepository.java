package com.example.bankcards.repository;

import com.example.bankcards.entity.Token;
import com.example.bankcards.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Token findByToken(String token);

    Token findByUser(User user);

    void deleteByToken(String token);
}