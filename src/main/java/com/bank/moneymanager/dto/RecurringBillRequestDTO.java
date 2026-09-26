package com.bank.moneymanager.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.bank.moneymanager.enums.BillingFrequency;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecurringBillRequestDTO {

    @NotBlank(message = "Biller name is required")
    @Size(max = 100, message = "Biller name cannot exceed 100 characters")
    private String billerName;

    @NotNull(message = "Account ID is required")
    private Long accountId;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "Billing frequency is required")
    private BillingFrequency frequency;

    @NotNull(message = "Next due date is required")
    private LocalDate nextDueDate;

    private Boolean autoDebit = false;
}