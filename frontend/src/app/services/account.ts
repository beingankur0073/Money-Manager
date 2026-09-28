import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

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

  constructor(private http: HttpClient) {}

  getAccounts(): Observable<Account[]> {
    return this.http.get<Account[]>(this.apiUrl);
  }

  createAccount(account: CreateAccountRequest): Observable<Account> {
    return this.http.post<Account>(this.apiUrl, account);
  }

  deleteAccount(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}