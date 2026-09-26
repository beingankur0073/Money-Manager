import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AccountService, Account } from '../../services/account';
import { TransactionService, Transaction, Category } from '../../services/transaction';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class DashboardComponent implements OnInit {
  accounts: Account[] = [];
  transactions: Transaction[] = [];
  categories: Category[] = [];

  accountForm: FormGroup;
  transactionForm: FormGroup;
  categoryForm: FormGroup;
  deleteAccountForm: FormGroup;

  accountToDelete: Account | null = null;

  errorMessage: string = '';
  successMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private accountService: AccountService,
    private transactionService: TransactionService,
    private cdr: ChangeDetectorRef
  ) {
    // Validations mapped strictly to AccountRequestDTO requirements
    this.accountForm = this.fb.group({
      accountName: ['', [Validators.required, Validators.maxLength(100)]],
      bankName: ['', Validators.required],
      accountNumber: ['', [Validators.required, Validators.pattern('^[0-9]{9,18}$')]],
      ifscCode: ['', [Validators.required, Validators.pattern('^[A-Z]{4}0[A-Z0-9]{6}$')]],
      bankLogoUrl: [''],
      accountType: ['SAVINGS', Validators.required],
      initialBalance: [0, [Validators.required, Validators.min(0)]],
      currency: ['INR', [Validators.required, Validators.minLength(3), Validators.maxLength(3)]]
    });

    // Validations mapped to TransactionRequestDTO
    this.transactionForm = this.fb.group({
      accountId: ['', Validators.required],
      categoryId: ['', Validators.required],
      recurringBillId: [null],
      amount: [0.01, [Validators.required, Validators.min(0.01)]],
      transactionType: ['DEBIT', Validators.required],
      transactionDate: [new Date().toISOString().split('T')[0], Validators.required],
      notes: ['', [Validators.maxLength(255)]]
    });

    this.categoryForm = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(50)]],
      type: ['EXPENSE', Validators.required]
    });

    this.deleteAccountForm = this.fb.group({
      confirmName: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.accountService.getAccounts().subscribe({
      next: (data) => {
        this.accounts = data;
        this.cdr.markForCheck();
      },
      error: (err: any) => this.handleError(err)
    });

    this.transactionService.getTransactions().subscribe({
      next: (data) => {
        this.transactions = data;
        this.cdr.markForCheck();
      },
      error: (err: any) => this.handleError(err)
    });

    this.transactionService.getCategories().subscribe({
      next: (data) => {
        this.categories = data;
        this.cdr.markForCheck();
      },
      error: (err: any) => this.handleError(err)
    });
  }

  onAddAccount(): void {
    if (this.accountForm.invalid) return;
    this.resetAlerts();

    // Uppercase IFSC Code automatically
    const formVal = {
      ...this.accountForm.value,
      ifscCode: this.accountForm.value.ifscCode.toUpperCase()
    };

    this.accountService.createAccount(formVal).subscribe({
      next: (acc) => {
        this.accounts.push(acc);
        this.accountForm.reset({
          accountType: 'SAVINGS',
          initialBalance: 0,
          currency: 'INR'
        });
        this.successMessage = 'Account created successfully!';
        this.cdr.markForCheck();
      },
      error: (err: any) => this.handleError(err)
    });
  }

  // --- Delete Account Modal ---

  openDeleteModal(account: Account): void {
    this.accountToDelete = account;
    this.deleteAccountForm.reset();
    this.cdr.markForCheck();
  }

  closeDeleteModal(): void {
    this.accountToDelete = null;
    this.deleteAccountForm.reset();
    this.cdr.markForCheck();
  }

  onConfirmDeleteAccount(): void {
    if (!this.accountToDelete || this.deleteAccountForm.invalid) return;

    if (this.deleteAccountForm.value.confirmName !== this.accountToDelete.accountName) {
      this.errorMessage = 'Account name does not match.';
      return;
    }

    const accountId = this.accountToDelete.id;
    this.resetAlerts();

    this.accountService.deleteAccount(accountId).subscribe({
      next: () => {
        this.accounts = this.accounts.filter(a => a.id !== accountId);
        this.transactions = this.transactions.filter(t => t.accountId !== accountId);

        if (this.transactionForm.get('accountId')?.value == accountId) {
          this.transactionForm.patchValue({ accountId: '' });
        }

        this.successMessage = 'Account and associated transactions deleted successfully!';
        this.closeDeleteModal();
        this.loadData();
      },
      error: (err: any) => {
        this.handleError(err);
        this.closeDeleteModal();
      }
    });
  }

  // --- Transaction Handling ---

  onAddTransaction(): void {
    if (this.transactionForm.invalid) return;
    this.resetAlerts();

    const formVal = this.transactionForm.value;
    const payload = {
      accountId: Number(formVal.accountId),
      categoryId: Number(formVal.categoryId),
      recurringBillId: formVal.recurringBillId ? Number(formVal.recurringBillId) : null,
      amount: Number(formVal.amount),
      transactionType: formVal.transactionType,
      transactionDate: formVal.transactionDate,
      notes: formVal.notes
    };

    this.transactionService.createTransaction(payload).subscribe({
      next: () => {
        this.transactionForm.reset({
          transactionType: 'DEBIT',
          amount: 0.01,
          transactionDate: new Date().toISOString().split('T')[0]
        });
        this.successMessage = 'Transaction recorded successfully!';
        this.loadData();
      },
      error: (err: any) => this.handleError(err)
    });
  }

  // --- Category Methods ---

  onAddCategory(): void {
    if (this.categoryForm.invalid) return;
    this.resetAlerts();

    this.transactionService.createCategory(this.categoryForm.value).subscribe({
      next: (newCategory) => {
        this.categories.push(newCategory);
        this.categoryForm.reset({ type: 'EXPENSE' });
        this.successMessage = 'Custom category created successfully!';
        this.cdr.markForCheck();
      },
      error: (err: any) => this.handleError(err)
    });
  }

  onDeleteCategory(categoryId: number): void {
    if (!confirm('Are you sure you want to delete this category?')) return;
    this.resetAlerts();

    this.transactionService.deleteCategory(categoryId).subscribe({
      next: () => {
        this.categories = this.categories.filter(c => c.id !== categoryId);
        this.successMessage = 'Category deleted successfully!';

        if (this.transactionForm.get('categoryId')?.value == categoryId) {
          this.transactionForm.patchValue({ categoryId: '' });
        }
        this.cdr.markForCheck();
      },
      error: (err: any) => this.handleError(err)
    });
  }

  private resetAlerts(): void {
    this.errorMessage = '';
    this.successMessage = '';
    this.cdr.markForCheck();
  }

  private handleError(err: any): void {
    if (err.error && err.error.message) {
      this.errorMessage = err.error.message;
    } else {
      this.errorMessage = 'An unexpected error occurred.';
    }
    this.cdr.markForCheck();
  }
}