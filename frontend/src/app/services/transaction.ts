import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Category {
  id: number;
  name: string;
  type: string;
}

export interface Transaction {
  id: number;
  amount: number;
  type?: 'DEBIT' | 'CREDIT';
  transactionType?: 'DEBIT' | 'CREDIT';
  description: string;
  transactionDate: string;
  categoryName?: string;
  accountName?: string;
}

export interface TransactionRequest {
  accountId: number;
  categoryId: number;
  amount: number;
  transactionType: 'DEBIT' | 'CREDIT';
  transactionDate: string;
  description: string;
}

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  private transactionUrl = '/api/transactions';
  private categoryUrl = '/api/categories';

  constructor(private http: HttpClient) {}

  getTransactions(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(this.transactionUrl);
  }

  createTransaction(payload: TransactionRequest): Observable<Transaction> {
    return this.http.post<Transaction>(this.transactionUrl, payload);
  }

  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(this.categoryUrl);
  }
}
