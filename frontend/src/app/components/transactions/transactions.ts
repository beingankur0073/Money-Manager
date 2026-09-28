import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators
} from '@angular/forms';

import {
  AccountService,
  Account
} from '../../services/account';

import {
  TransactionService,
  Transaction,
  Category
} from '../../services/transaction';

import { NotificationService } from '../../services/notification';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './transactions.html',
  styleUrl: './transactions.scss'
})
export class TransactionsComponent implements OnInit {

  accounts: Account[] = [];

  transactions: Transaction[] = [];

  categories: Category[] = [];

  transactionForm: FormGroup;


  constructor(
    private fb: FormBuilder,
    private accountService: AccountService,
    private transactionService: TransactionService,
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef
  ) {

    // Transaction form
    this.transactionForm = this.fb.group({

      accountId: [
        '',
        Validators.required
      ],

      categoryId: [
        '',
        Validators.required
      ],

      recurringBillId: [
        null
      ],

      amount: [
        0.01,
        [
          Validators.required,
          Validators.min(0.01)
        ]
      ],

      transactionType: [
        'DEBIT',
        Validators.required
      ],

      transactionDate: [
        new Date().toISOString().split('T')[0],
        Validators.required
      ],

      notes: [
        '',
        Validators.maxLength(255)
      ]

    });

  }


  ngOnInit(): void {

    this.loadData();

  }


  // ================= LOAD DATA =================

  loadData(): void {

    this.accountService
      .getAccounts()
      .subscribe({

        next: (data) => {

          this.accounts = data;

          this.cdr.markForCheck();

        },

        error: (err: any) => {

          this.handleError(err);

        }

      });


    this.transactionService
      .getTransactions()
      .subscribe({

        next: (data) => {

          this.transactions = data;

          this.cdr.markForCheck();

        },

        error: (err: any) => {

          this.handleError(err);

        }

      });


    this.transactionService
      .getCategories()
      .subscribe({

        next: (data) => {

          this.categories = data;

          this.cdr.markForCheck();

        },

        error: (err: any) => {

          this.handleError(err);

        }

      });

  }


  // ================= ADD TRANSACTION =================

  onAddTransaction(): void {

    if (this.transactionForm.invalid) {

      this.transactionForm.markAllAsTouched();

      return;
    }


    const formVal =
      this.transactionForm.value;


    const payload = {

      accountId:
        Number(formVal.accountId),

      categoryId:
        Number(formVal.categoryId),

      recurringBillId:
        formVal.recurringBillId
          ? Number(formVal.recurringBillId)
          : null,

      amount:
        Number(formVal.amount),

      transactionType:
        formVal.transactionType,

      transactionDate:
        formVal.transactionDate,

      notes:
        formVal.notes

    };


    this.transactionService
      .createTransaction(payload)
      .subscribe({

        next: () => {

          this.transactionForm.reset({

            transactionType: 'DEBIT',

            amount: 0.01,

            transactionDate:
              new Date()
                .toISOString()
                .split('T')[0]

          });


          this.notificationService.success(
            'Transaction recorded successfully!'
          );


          this.loadData();

        },

        error: (err: any) => {

          this.handleError(err);

        }

      });

  }


  // ================= ERROR HANDLING =================

  private handleError(err: any): void {

    const message =
      err?.error?.message ||
      'An unexpected error occurred.';

    this.notificationService.error(message);

  }

}