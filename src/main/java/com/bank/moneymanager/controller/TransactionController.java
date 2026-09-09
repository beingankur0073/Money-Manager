package com.bank.moneymanager.controller;

import com.bank.moneymanager.dto.TransactionRequestDTO;
import com.bank.moneymanager.dto.TransactionResponseDTO;
import com.bank.moneymanager.security.UserPrincipal;
import com.bank.moneymanager.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> postTransaction(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody TransactionRequestDTO requestDTO) {

        TransactionResponseDTO response = transactionService.postTransaction(principal.getId(), requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getTransactions(
            @AuthenticationPrincipal UserPrincipal principal) {

        List<TransactionResponseDTO> transactions = transactionService.getTransactionsByUser(principal.getId());
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsByDateRange(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<TransactionResponseDTO> transactions =
                transactionService.getTransactionsByDateRange(principal.getId(), startDate, endDate);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsByAccount(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long accountId) {

        List<TransactionResponseDTO> transactions =
                transactionService.getTransactionsByAccount(accountId, principal.getId());
        return ResponseEntity.ok(transactions);
    }
}