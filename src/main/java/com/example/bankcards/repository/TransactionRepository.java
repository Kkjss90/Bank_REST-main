package com.example.bankcards.repository;

import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT t FROM Transaction t WHERE t.fromCard.user.id = :userId")
    List<Transaction> findByFromAccountUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM Transaction t WHERE t.toCard.user.id = :userId")
    List<Transaction> findByToAccountUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM Transaction t WHERE t.fromCard.user.id = :fromUserId OR t.toCard.user.id = :toUserId")
    List<Transaction> findByFromAccountUserIdOrToAccountUserId(@Param("fromUserId") Long fromUserId, @Param("toUserId") Long toUserId);

    @Query("SELECT t FROM Transaction t WHERE t.status = :status")
    List<Transaction> findByStatus(@Param("status") TransactionStatus status);
}