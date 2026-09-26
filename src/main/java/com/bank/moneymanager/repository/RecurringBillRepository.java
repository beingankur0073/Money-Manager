package com.bank.moneymanager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.moneymanager.entity.RecurringBill;

@Repository
public interface RecurringBillRepository extends JpaRepository<RecurringBill, Long> {

    // Strictly enforce tenant boundary: fetch bills belonging ONLY to the user
    List<RecurringBill> findAllByUserId(Long userId);

    // IDOR protection: access a bill ONLY if it belongs to the requesting user
    Optional<RecurringBill> findByIdAndUserId(Long id, Long userId);
}