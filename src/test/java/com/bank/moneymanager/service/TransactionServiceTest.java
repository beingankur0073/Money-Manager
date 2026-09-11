package com.bank.moneymanager.service;

import com.bank.moneymanager.dto.TransactionRequestDTO;
import com.bank.moneymanager.dto.TransactionResponseDTO;
import com.bank.moneymanager.entity.Account;
import com.bank.moneymanager.entity.Category;
import com.bank.moneymanager.entity.Transaction;
import com.bank.moneymanager.entity.User;
import com.bank.moneymanager.enums.TransactionType;
import com.bank.moneymanager.exception.InsufficientFundsException;
import com.bank.moneymanager.repository.AccountRepository;
import com.bank.moneymanager.repository.CategoryRepository;
import com.bank.moneymanager.repository.TransactionRepository;
import com.bank.moneymanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User user;
    private Account account;
    private Category category;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        account = new Account();
        account.setId(100L);
        account.setUser(user);
        account.setCurrentBalance(new BigDecimal("1000.00")); // Starting balance ₹1000

        category = new Category();
        category.setId(5L);

        Transaction mockSavedTransaction = new Transaction();
        mockSavedTransaction.setId(10L);
        mockSavedTransaction.setAmount(new BigDecimal("200.00"));
        // Changed from setType to setTransactionType
        mockSavedTransaction.setTransactionType(TransactionType.DEBIT);
        mockSavedTransaction.setAccount(account);
        mockSavedTransaction.setCategory(category);
    }

    @Test
    void postTransaction_ValidDebit_SubtractsBalanceAndSaves() {
        // 1. Arrange
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setAccountId(100L);
        request.setCategoryId(5L);
        request.setAmount(new BigDecimal("200.00"));
        // Changed to setTransactionType and passing the Enum directly
        request.setTransactionType(TransactionType.DEBIT);
        // Removed setDescription as it's not required for this math test

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(accountRepository.findByIdAndUserId(100L, 1L)).thenReturn(Optional.of(account));
        when(categoryRepository.findById(5L)).thenReturn(Optional.of(category));

        Transaction savedTx = new Transaction();
        savedTx.setId(99L);
        savedTx.setAccount(account);
        savedTx.setCategory(category);
        savedTx.setAmount(new BigDecimal("200.00"));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTx);

        // 2. Act
        TransactionResponseDTO response = transactionService.postTransaction(1L, request);

        // 3. Assert
        assertNotNull(response, "Response should not be null"); // Fixes the 'variable is never used' warning
        assertEquals(new BigDecimal("800.00"), account.getCurrentBalance(), "Balance should decrease by 200");
        verify(accountRepository, times(1)).save(account);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void postTransaction_DebitExceedsBalance_ThrowsException() {
        // 1. Arrange
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setAccountId(100L);
        request.setCategoryId(5L);
        request.setAmount(new BigDecimal("1500.00")); // Trying to spend 1500 on a 1000 balance
        request.setTransactionType(TransactionType.DEBIT);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(accountRepository.findByIdAndUserId(100L, 1L)).thenReturn(Optional.of(account));
        when(categoryRepository.findById(5L)).thenReturn(Optional.of(category));

        // 2 & 3. Act and Assert
        // Fixed the lambda warning by removing the curly braces
        assertThrows(InsufficientFundsException.class,
                () -> transactionService.postTransaction(1L, request),
                "Should throw InsufficientFundsException when debit exceeds balance");

        // Verify that we NEVER saved anything to the database during a failure
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void postTransaction_ValidCredit_AddsToBalance() {
        // 1. Arrange
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setAccountId(100L);
        request.setCategoryId(5L);
        request.setAmount(new BigDecimal("500.00"));
        request.setTransactionType(TransactionType.CREDIT);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(accountRepository.findByIdAndUserId(100L, 1L)).thenReturn(Optional.of(account));
        when(categoryRepository.findById(5L)).thenReturn(Optional.of(category));

        Transaction savedTx = new Transaction();
        savedTx.setId(99L);
        savedTx.setAccount(account);
        savedTx.setCategory(category);
        savedTx.setAmount(new BigDecimal("500.00"));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTx);

        // 2. Act
        TransactionResponseDTO response = transactionService.postTransaction(1L, request);

        // 3. Assert
        assertNotNull(response); // Fixes the 'variable is never used' warning
        assertEquals(new BigDecimal("1500.00"), account.getCurrentBalance(), "Balance should increase by 500");
    }
}