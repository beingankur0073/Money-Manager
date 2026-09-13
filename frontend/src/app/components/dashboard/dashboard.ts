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

  errorMessage: string = '';
  successMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private accountService: AccountService,
    private transactionService: TransactionService,
    private cdr: ChangeDetectorRef // 1. Inject ChangeDetectorRef
  ) {
    this.accountForm = this.fb.group({
      accountName: ['', Validators.required],
      accountType: ['CHECKING', Validators.required],
      initialBalance: [0, [Validators.required, Validators.min(0)]],
      currency: ['INR', Validators.required]
    });

    this.transactionForm = this.fb.group({
      accountId: ['', Validators.required],
      categoryId: ['', Validators.required],
      amount: [0.01, [Validators.required, Validators.min(0.01)]],
      transactionType: ['DEBIT', Validators.required],
      description: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.accountService.getAccounts().subscribe({
      next: (data) => {
        this.accounts = data;
        this.cdr.markForCheck(); // 2. Trigger UI render for zoneless mode
      },
      error: (err: any) => this.handleError(err)
    });

    this.transactionService.getTransactions().subscribe({
      next: (data) => {
        this.transactions = data;
        this.cdr.markForCheck(); // 2. Trigger UI render for zoneless mode
      },
      error: (err: any) => this.handleError(err)
    });

    this.transactionService.getCategories().subscribe({
      next: (data) => {
        this.categories = data;
        this.cdr.markForCheck(); // 2. Trigger UI render for zoneless mode
      },
      error: (err: any) => this.handleError(err)
    });
  }

  onAddAccount(): void {
    if (this.accountForm.invalid) return;
    this.resetAlerts();

    this.accountService.createAccount(this.accountForm.value).subscribe({
      next: (acc) => {
        this.accounts.push(acc);
        this.accountForm.reset({ accountType: 'CHECKING', initialBalance: 0, currency: 'INR' });
        this.successMessage = 'Account created successfully!';
        this.cdr.markForCheck();
      },
      error: (err: any) => this.handleError(err)
    });
  }

  onAddTransaction(): void {
    if (this.transactionForm.invalid) return;
    this.resetAlerts();

    const formVal = this.transactionForm.value;
    const payload = {
      accountId: Number(formVal.accountId),
      categoryId: Number(formVal.categoryId),
      amount: Number(formVal.amount),
      transactionType: formVal.transactionType,
      transactionDate: new Date().toISOString(),
      description: formVal.description
    };

    this.transactionService.createTransaction(payload).subscribe({
      next: () => {
        this.transactionForm.reset({ transactionType: 'DEBIT', amount: 0.01 });
        this.successMessage = 'Transaction recorded successfully!';
        this.loadData();
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
