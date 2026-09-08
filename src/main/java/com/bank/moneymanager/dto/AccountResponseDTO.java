package com.bank.moneymanager.dto;

import com.bank.moneymanager.enums.AccountType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponseDTO {
    private Long id;
    private String accountName;
    private AccountType accountType;
    private BigDecimal currentBalance;
    private String currency;
    private Long version;
    private LocalDateTime createdAt;
}