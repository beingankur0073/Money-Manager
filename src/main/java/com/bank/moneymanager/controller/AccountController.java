package com.bank.moneymanager.controller;

import com.bank.moneymanager.dto.AccountRequestDTO;
import com.bank.moneymanager.dto.AccountResponseDTO;
import com.bank.moneymanager.security.UserPrincipal;
import com.bank.moneymanager.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody AccountRequestDTO requestDTO) {

        AccountResponseDTO response = accountService.createAccount(principal.getId(), requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> getAccounts(
            @AuthenticationPrincipal UserPrincipal principal) {

        List<AccountResponseDTO> accounts = accountService.getAccountsByUser(principal.getId());
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> getAccountById(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {

        AccountResponseDTO account = accountService.getAccountById(id, principal.getId());
        return ResponseEntity.ok(account);
    }
}