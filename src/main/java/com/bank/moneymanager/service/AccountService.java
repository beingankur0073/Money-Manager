package com.bank.moneymanager.service;

import com.bank.moneymanager.dto.AccountRequestDTO;
import com.bank.moneymanager.dto.AccountResponseDTO;
import com.bank.moneymanager.entity.Account;
import com.bank.moneymanager.entity.User;
import com.bank.moneymanager.exception.ResourceNotFoundException;
import com.bank.moneymanager.repository.AccountRepository;
import com.bank.moneymanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Transactional
    public AccountResponseDTO createAccount(Long userId, AccountRequestDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Account account = new Account();
        account.setUser(user);
        account.setAccountName(dto.getAccountName().trim());
        account.setAccountType(dto.getAccountType());
        account.setCurrentBalance(dto.getInitialBalance());
        account.setCurrency(dto.getCurrency().toUpperCase().trim());

        Account savedAccount = accountRepository.save(account);
        return mapToDTO(savedAccount);
    }

    @Transactional(readOnly = true)
    public List<AccountResponseDTO> getAccountsByUser(Long userId) {
        return accountRepository.findAllByUserId(userId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccountResponseDTO getAccountById(Long accountId, Long userId) {
        Account account = findAccountByTenant(accountId, userId);
        return mapToDTO(account);
    }

    @Transactional(readOnly = true)
    public Account findAccountByTenant(Long accountId, Long userId) {
        return accountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Account not found with ID: " + accountId + " for the current user"));
    }

    private AccountResponseDTO mapToDTO(Account account) {
        return AccountResponseDTO.builder()
                .id(account.getId())
                .accountName(account.getAccountName())
                .accountType(account.getAccountType())
                .currentBalance(account.getCurrentBalance())
                .currency(account.getCurrency())
                .version(account.getVersion())
                .createdAt(account.getCreatedAt())
                .build();
    }
}