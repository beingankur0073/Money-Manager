package com.bank.moneymanager.repository;

import com.bank.moneymanager.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // Strictly enforce tenant boundary: fetch accounts belonging ONLY to the user
    List<Account> findAllByUserId(Long userId);

    // IDOR protection: access an account ONLY if it belongs to the requesting user
    Optional<Account> findByIdAndUserId(Long id, Long userId);
}