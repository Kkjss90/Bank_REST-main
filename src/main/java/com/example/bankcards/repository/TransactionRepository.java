package com.example.bankcards.repository;

import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query(value = "select * from transaction"
            + "where ", nativeQuery = true)
    List<Transaction> findByFromAccountUserId(Long userId);
    @Query(value = "select * from transaction"
            + "where ", nativeQuery = true)
    List<Transaction> findByToAccountUserId(Long userId);
    @Query(value = "select * from transaction"
            + "where ", nativeQuery = true)
    List<Transaction> findByFromAccountUserIdOrToAccountUserId(Long fromUserId, Long toUserId);
    @Query(value = "select * from transaction"
            + "where ", nativeQuery = true)
    List<Transaction> findByStatus(TransactionStatus status);
}