package com.bank.moneymanager.service;

import com.bank.moneymanager.dto.RecurringBillRequestDTO;
import com.bank.moneymanager.dto.RecurringBillResponseDTO;
import com.bank.moneymanager.entity.Account;
import com.bank.moneymanager.entity.Category;
import com.bank.moneymanager.entity.RecurringBill;
import com.bank.moneymanager.entity.Transaction;
import com.bank.moneymanager.entity.User;
import com.bank.moneymanager.enums.BillStatus;
import com.bank.moneymanager.enums.TransactionType;
import com.bank.moneymanager.exception.InsufficientFundsException;
import com.bank.moneymanager.exception.ResourceNotFoundException;
import com.bank.moneymanager.repository.AccountRepository;
import com.bank.moneymanager.repository.CategoryRepository;
import com.bank.moneymanager.repository.RecurringBillRepository;
import com.bank.moneymanager.repository.TransactionRepository;
import com.bank.moneymanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecurringBillService {

    private final RecurringBillRepository recurringBillRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public RecurringBillResponseDTO createRecurringBill(Long userId, RecurringBillRequestDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Account account = accountRepository.findByIdAndUserId(dto.getAccountId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + dto.getAccountId()));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + dto.getCategoryId()));

        RecurringBill bill = new RecurringBill();
        bill.setUser(user);
        bill.setDefaultAccount(account);
        bill.setCategory(category);
        bill.setBillerName(dto.getBillerName().trim());
        bill.setAmount(dto.getAmount());
        bill.setFrequency(dto.getFrequency());
        bill.setNextDueDate(dto.getNextDueDate());
        bill.setAutoDebit(dto.getAutoDebit() != null && dto.getAutoDebit());
        bill.setStatus(BillStatus.ACTIVE);

        RecurringBill savedBill = recurringBillRepository.save(bill);
        return mapToDTO(savedBill);
    }

    @Transactional(readOnly = true)
    public List<RecurringBillResponseDTO> getRecurringBillsByUser(Long userId) {
        return recurringBillRepository.findAllByUserId(userId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public RecurringBillResponseDTO getRecurringBillById(Long billId, Long userId) {
        RecurringBill bill = findBillByTenant(billId, userId);
        return mapToDTO(bill);
    }

    @Transactional
    public void deleteRecurringBill(Long billId, Long userId) {
        RecurringBill bill = findBillByTenant(billId, userId);
        recurringBillRepository.delete(bill);
    }

    @Transactional
    public RecurringBillResponseDTO payRecurringBill(Long billId, Long userId) {
        RecurringBill bill = findBillByTenant(billId, userId);
        Account account = bill.getDefaultAccount();

        // 1. Overdraft Guard Check
        if (account.getCurrentBalance().compareTo(bill.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient balance in '" 
                    + account.getAccountName() + "' to execute bill payment for " + bill.getBillerName());
        }

        // 2. Post DEBIT Transaction
        Transaction transaction = new Transaction();
        transaction.setUser(bill.getUser());
        transaction.setAccount(account);
        transaction.setCategory(bill.getCategory());
        transaction.setRecurringBill(bill);
        transaction.setAmount(bill.getAmount());
        transaction.setTransactionType(TransactionType.DEBIT);
        transaction.setTransactionDate(LocalDate.now());
        transaction.setNotes("Recurring Bill Payment: " + bill.getBillerName());

        // 3. Update Account Balance & Save Transaction
        account.setCurrentBalance(account.getCurrentBalance().subtract(bill.getAmount()));
        accountRepository.save(account);
        transactionRepository.save(transaction);

        // 4. Advance Next Due Date based on Frequency
        LocalDate nextDate = switch (bill.getFrequency()) {
            case WEEKLY -> bill.getNextDueDate().plusWeeks(1);
            case MONTHLY -> bill.getNextDueDate().plusMonths(1);
            case QUARTERLY -> bill.getNextDueDate().plusMonths(3);
            case ANNUALLY -> bill.getNextDueDate().plusYears(1);
        };
        bill.setNextDueDate(nextDate);

        RecurringBill updatedBill = recurringBillRepository.save(bill);
        return mapToDTO(updatedBill);
    }

    private RecurringBill findBillByTenant(Long billId, Long userId) {
        return recurringBillRepository.findByIdAndUserId(billId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Recurring bill not found with ID: " + billId + " for current user"));
    }

    private RecurringBillResponseDTO mapToDTO(RecurringBill bill) {
        return RecurringBillResponseDTO.builder()
                .id(bill.getId())
                .billerName(bill.getBillerName())
                .accountId(bill.getDefaultAccount().getId())
                .accountName(bill.getDefaultAccount().getAccountName())
                .bankName(bill.getDefaultAccount().getBankName())
                .categoryId(bill.getCategory().getId())
                .categoryName(bill.getCategory().getName())
                .amount(bill.getAmount())
                .frequency(bill.getFrequency())
                .nextDueDate(bill.getNextDueDate())
                .autoDebit(bill.getAutoDebit())
                .status(bill.getStatus())
                .createdAt(bill.getCreatedAt())
                .build();
    }
}