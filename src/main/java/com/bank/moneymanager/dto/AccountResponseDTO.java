package com.bank.moneymanager.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bank.moneymanager.enums.AccountType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDTO {
    private Long id;
    private String accountName;
    private String bankName;
    private String accountNumber; // Can be masked in UI (e.g. "•••• •••• 5678")
    private String ifscCode;
    private String bankLogoUrl;
    private AccountType accountType;
    private BigDecimal currentBalance;
    private String currency;
    private Long version;
    private LocalDateTime createdAt;
}