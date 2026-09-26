import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { MatSnackBar } from '@angular/material/snack-bar';

// Matches CategoryResponseDTO
export interface Category {
  id: number;
  name: string;
  type: string;
  isSystemDefault?: boolean;
}

// Matches CategoryRequestDTO
export interface CategoryRequest {
  name: string;
  type: 'EXPENSE' | 'INCOME';
}

// Matches TransactionResponseDTO
export interface Transaction {
  id: number;
  accountId: number;
  accountName: string;
  categoryId: number;
  categoryName: string;
  amount: number;
  transactionType: 'DEBIT' | 'CREDIT';
  transactionDate: string;
  notes: string;
  createdAt?: string;
}

// Matches TransactionRequestDTO
export interface TransactionRequest {
  accountId: number;
  categoryId: number;
  amount: number;
  transactionType: 'DEBIT' | 'CREDIT';
  transactionDate: string;
  notes: string;
}

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  private transactionUrl = '/api/transactions';
  private categoryUrl = '/api/categories';

  constructor(
    private http: HttpClient,
    private snackBar: MatSnackBar
  ) {}

  // --- Transaction Methods ---

  getTransactions(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(this.transactionUrl);
  }

  createTransaction(payload: TransactionRequest): Observable<Transaction> {
    return this.http.post<Transaction>(this.transactionUrl, payload).pipe(
      tap({
        next: (res) => {
          this.snackBar.open(
            `Transaction recorded successfully! (₹${res.amount || payload.amount})`,
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
          const msg = err?.error?.message || 'Failed to record transaction.';
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

  // --- Category Methods ---

  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(this.categoryUrl);
  }

  createCategory(payload: CategoryRequest): Observable<Category> {
    return this.http.post<Category>(this.categoryUrl, payload).pipe(
      tap({
        next: (res) => {
          this.snackBar.open(
            `Category "${res.name || payload.name}" created successfully!`,
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
          const msg = err?.error?.message || 'Failed to create category.';
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

  deleteCategory(categoryId: number): Observable<void> {
    return this.http.delete<void>(`${this.categoryUrl}/${categoryId}`).pipe(
      tap({
        next: () => {
          this.snackBar.open('Category deleted successfully.', 'Close', {
            duration: 3000,
            panelClass: ['toast-info'],
            horizontalPosition: 'center',
            verticalPosition: 'top'
          });
        },
        error: (err) => {
          const msg = err?.error?.message || 'Failed to delete category.';
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
