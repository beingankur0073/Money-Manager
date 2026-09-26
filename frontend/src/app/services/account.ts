import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { MatSnackBar } from '@angular/material/snack-bar';

export interface Account {
  id: number;
  accountName: string;
  bankName: string;
  accountNumber: string;
  ifscCode: string;
  bankLogoUrl?: string;
  accountType: 'CHECKING' | 'SAVINGS' | 'CREDIT_CARD';
  currentBalance: number;
  currency: string;
  version?: number;
  createdAt?: string;
}

export interface CreateAccountRequest {
  accountName: string;
  bankName: string;
  accountNumber: string;
  ifscCode: string;
  bankLogoUrl?: string;
  accountType: 'CHECKING' | 'SAVINGS' | 'CREDIT_CARD';
  initialBalance: number;
  currency: string;
}

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private apiUrl = '/api/accounts';

  constructor(
    private http: HttpClient,
    private snackBar: MatSnackBar
  ) {}

  getAccounts(): Observable<Account[]> {
    return this.http.get<Account[]>(this.apiUrl);
  }

  createAccount(account: CreateAccountRequest): Observable<Account> {
    return this.http.post<Account>(this.apiUrl, account).pipe(
      tap({
        next: (res) => {
          this.snackBar.open(
            `Account "${res.accountName || account.accountName}" created successfully!`,
            'Close',
            {
              duration: 3500,
              panelClass: ['toast-success'],
              horizontalPosition: 'center',
              verticalPosition: 'top'
            }
          );
        },
        error: (err) => {
          const msg = err?.error?.message || 'Failed to create account.';
          this.snackBar.open(msg, 'Close', {
            duration: 4000,
            panelClass: ['toast-error'],
            horizontalPosition: 'center',
            verticalPosition: 'top'
          });
        }
      })
    );
  }

  deleteAccount(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      tap({
        next: () => {
          this.snackBar.open('Account deleted successfully!', 'Close', {
            duration: 3500,
            panelClass: ['toast-success'],
            horizontalPosition: 'center',
            verticalPosition: 'top'
          });
        },
        error: (err) => {
          const msg = err?.error?.message || 'Failed to delete account.';
          this.snackBar.open(msg, 'Close', {
            duration: 4000,
            panelClass: ['toast-error'],
            horizontalPosition: 'center',
            verticalPosition: 'top'
          });
        }
      })
    );
  }
}