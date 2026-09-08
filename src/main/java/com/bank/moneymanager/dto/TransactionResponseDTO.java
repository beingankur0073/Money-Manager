package com.bank.moneymanager.dto;

import com.bank.moneymanager.enums.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseDTO {
    private Long id;
    private Long accountId;
    private String accountName;
    private Long categoryId;
    private String categoryName;
    private BigDecimal amount;
    private TransactionType transactionType;
    private LocalDate transactionDate;
    private String notes;
    private LocalDateTime createdAt;
}