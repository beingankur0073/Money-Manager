package com.bank.moneymanager.entity;

import com.bank.moneymanager.enums.BillStatus;
import com.bank.moneymanager.enums.BillingFrequency;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "recurring_bills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecurringBill extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account defaultAccount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "biller_name", nullable = false, length = 100)
    private String billerName; // e.g., "CESC Electricity", "Airtel Broadband", "House Rent"

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency", nullable = false, length = 20)
    private BillingFrequency frequency;

    @Column(name = "next_due_date", nullable = false)
    private LocalDate nextDueDate;

    @Column(name = "auto_debit", nullable = false)
    private Boolean autoDebit = false; // If true, can be auto-processed via Spring @Scheduled job

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BillStatus status = BillStatus.ACTIVE;
}