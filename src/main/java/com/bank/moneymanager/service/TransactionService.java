package com.bank.moneymanager.service;

import com.bank.moneymanager.dto.TransactionRequestDTO;
import com.bank.moneymanager.dto.TransactionResponseDTO;
import com.bank.moneymanager.entity.Account;
import com.bank.moneymanager.entity.Category;
import com.bank.moneymanager.entity.Transaction;
import com.bank.moneymanager.entity.User;
import com.bank.moneymanager.enums.TransactionType;
import com.bank.moneymanager.exception.InsufficientFundsException;
import com.bank.moneymanager.exception.ResourceNotFoundException;
import com.bank.moneymanager.repository.AccountRepository;
import com.bank.moneymanager.repository.CategoryRepository;
import com.bank.moneymanager.repository.TransactionRepository;
import com.bank.moneymanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public TransactionResponseDTO postTransaction(Long userId, TransactionRequestDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Account account = accountRepository.findByIdAndUserId(dto.getAccountId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Account not found with ID: " + dto.getAccountId() + " for this user"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .filter(cat -> cat.getUser() == null || cat.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found or unauthorized with ID: " + dto.getCategoryId()));

        BigDecimal amount = dto.getAmount();

        if (dto.getTransactionType() == TransactionType.DEBIT) {
            if (account.getCurrentBalance().compareTo(amount) < 0) {
                throw new InsufficientFundsException(
                        String.format("Insufficient funds. Account balance: %s, Requested debit: %s",
                                account.getCurrentBalance(), amount));
            }
            account.setCurrentBalance(account.getCurrentBalance().subtract(amount));
        } else if (dto.getTransactionType() == TransactionType.CREDIT) {
            account.setCurrentBalance(account.getCurrentBalance().add(amount));
        }

        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setAmount(amount);
        transaction.setTransactionType(dto.getTransactionType());
        transaction.setTransactionDate(dto.getTransactionDate());
        transaction.setNotes(dto.getNotes() != null ? dto.getNotes().trim() : null);

        Transaction savedTransaction = transactionRepository.save(transaction);

        return mapToDTO(savedTransaction);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getTransactionsByUser(Long userId) {
        return transactionRepository.findAllByUserIdOrderByTransactionDateDesc(userId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getTransactionsByDateRange(
            Long userId, LocalDate startDate, LocalDate endDate) {
        return transactionRepository.findAllByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(
                        userId, startDate, endDate)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getTransactionsByAccount(Long accountId, Long userId) {
        accountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Account not found with ID: " + accountId + " for this user"));

        return transactionRepository.findAllByAccountIdAndUserId(accountId, userId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    private TransactionResponseDTO mapToDTO(Transaction tx) {
        return TransactionResponseDTO.builder()
                .id(tx.getId())
                .accountId(tx.getAccount().getId())
                .accountName(tx.getAccount().getAccountName())
                .categoryId(tx.getCategory().getId())
                .categoryName(tx.getCategory().getName())
                .amount(tx.getAmount())
                .transactionType(tx.getTransactionType())
                .transactionDate(tx.getTransactionDate())
                .notes(tx.getNotes())
                .createdAt(tx.getCreatedAt())
                .build();
    }
}