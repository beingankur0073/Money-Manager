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

import { NotificationService } from '../../services/notification';

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './accounts.html',
  styleUrl: './accounts.scss'
})
export class AccountsComponent implements OnInit {

  accounts: Account[] = [];

  accountForm: FormGroup;
  deleteAccountForm: FormGroup;

  accountToDelete: Account | null = null;

  constructor(
    private fb: FormBuilder,
    private accountService: AccountService,
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef
  ) {

    // Account form
    this.accountForm = this.fb.group({

      accountName: [
        '',
        [
          Validators.required,
          Validators.maxLength(100)
        ]
      ],

      bankName: [
        '',
        Validators.required
      ],

      accountNumber: [
        '',
        [
          Validators.required,
          Validators.pattern('^[0-9]{9,18}$')
        ]
      ],

      ifscCode: [
        '',
        [
          Validators.required,
          Validators.pattern('^[A-Z]{4}0[A-Z0-9]{6}$')
        ]
      ],

      bankLogoUrl: [''],

      accountType: [
        'SAVINGS',
        Validators.required
      ],

      initialBalance: [
        0,
        [
          Validators.required,
          Validators.min(0)
        ]
      ],

      currency: [
        'INR',
        [
          Validators.required,
          Validators.minLength(3),
          Validators.maxLength(3)
        ]
      ]

    });


    // Delete account form
    this.deleteAccountForm = this.fb.group({

      confirmName: [
        '',
        Validators.required
      ]

    });

  }


  ngOnInit(): void {
    this.loadAccounts();
  }


  // ================= LOAD ACCOUNTS =================

  loadAccounts(): void {

    this.accountService.getAccounts().subscribe({

      next: (data) => {

        this.accounts = data;

        this.cdr.markForCheck();
      },

      error: (err: any) => {

        this.handleError(err);
      }

    });

  }


  // ================= ADD ACCOUNT =================

  onAddAccount(): void {

    if (this.accountForm.invalid) {

      this.accountForm.markAllAsTouched();

      return;
    }


    const formVal = {

      ...this.accountForm.value,

      ifscCode:
        this.accountForm.value.ifscCode.toUpperCase()

    };


    this.accountService
      .createAccount(formVal)
      .subscribe({

        next: (acc) => {

          this.accounts.push(acc);


          this.accountForm.reset({

            accountType: 'SAVINGS',

            initialBalance: 0,

            currency: 'INR'

          });


          this.notificationService.success(
            'Account created successfully!'
          );


          this.cdr.markForCheck();
        },

        error: (err: any) => {

          this.handleError(err);
        }

      });

  }


  // ================= DELETE ACCOUNT =================

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

    if (
      !this.accountToDelete ||
      this.deleteAccountForm.invalid
    ) {

      return;
    }


    if (
      this.deleteAccountForm.value.confirmName !==
      this.accountToDelete.accountName
    ) {

      this.notificationService.error(
        'Account name does not match.'
      );

      return;
    }


    const accountId =
      this.accountToDelete.id;


    this.accountService
      .deleteAccount(accountId)
      .subscribe({

        next: () => {

          this.accounts =
            this.accounts.filter(
              account => account.id !== accountId
            );


          this.notificationService.success(
            'Account and associated transactions deleted successfully!'
          );


          this.closeDeleteModal();


          this.loadAccounts();

        },

        error: (err: any) => {

          this.handleError(err);

          this.closeDeleteModal();

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