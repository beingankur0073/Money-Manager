package com.bank.moneymanager.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.bank.moneymanager.enums.BillStatus;
import com.bank.moneymanager.enums.BillingFrequency;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecurringBillResponseDTO {
    private Long id;
    private String billerName;
    private Long accountId;
    private String accountName;
    private String bankName;
    private Long categoryId;
    private String categoryName;
    private BigDecimal amount;
    private BillingFrequency frequency;
    private LocalDate nextDueDate;
    private Boolean autoDebit;
    private BillStatus status;
    private LocalDateTime createdAt;
}