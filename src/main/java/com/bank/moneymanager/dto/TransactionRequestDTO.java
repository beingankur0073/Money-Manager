package com.bank.moneymanager.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.bank.moneymanager.enums.TransactionType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequestDTO {

    @NotNull(message = "Account ID is required")
    private Long accountId;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    // Optional link to a Recurring Bill
    private Long recurringBillId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "Transaction amount must be at least 0.01")
    @Digits(integer = 15, fraction = 4, message = "Amount out of bounds: maximum 15 integer digits and 4 decimal places")
    private BigDecimal amount;

    @NotNull(message = "Transaction type is required (DEBIT or CREDIT)")
    private TransactionType transactionType;

    @NotNull(message = "Transaction date is required")
    @PastOrPresent(message = "Transaction date cannot be in the future")
    private LocalDate transactionDate;

    @Size(max = 255, message = "Notes cannot exceed 255 characters")
    private String notes;
}