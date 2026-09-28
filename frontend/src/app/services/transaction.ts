import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

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

  constructor(private http: HttpClient) {}

  // --- Transaction Methods ---

  getTransactions(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(this.transactionUrl);
  }

  createTransaction(
    payload: TransactionRequest
  ): Observable<Transaction> {
    return this.http.post<Transaction>(
      this.transactionUrl,
      payload
    );
  }

  // --- Category Methods ---

  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(this.categoryUrl);
  }

  createCategory(
    payload: CategoryRequest
  ): Observable<Category> {
    return this.http.post<Category>(
      this.categoryUrl,
      payload
    );
  }

  deleteCategory(
    categoryId: number
  ): Observable<void> {
    return this.http.delete<void>(
      `${this.categoryUrl}/${categoryId}`
    );
  }
}