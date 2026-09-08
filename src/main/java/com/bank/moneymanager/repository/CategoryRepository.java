package com.bank.moneymanager.repository;

import com.bank.moneymanager.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Return system categories (user_id IS NULL) + user's custom categories
    List<Category> findByUserIdOrUserIdIsNull(Long userId);

    // User can only modify or delete categories they own
    Optional<Category> findByIdAndUserId(Long id, Long userId);
}