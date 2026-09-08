package com.bank.moneymanager.repository;

import com.bank.moneymanager.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Ordered chronological ledger scoped to the authenticated user
    List<Transaction> findAllByUserIdOrderByTransactionDateDesc(Long userId);

    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    List<Transaction> findAllByAccountIdAndUserId(Long accountId, Long userId);

    List<Transaction> findAllByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(
            Long userId, LocalDate startDate, LocalDate endDate);
}