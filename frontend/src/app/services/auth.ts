import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = '/api/auth';
  private tokenKey = 'token';

  constructor(private http: HttpClient) {}

  register(userData: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/register`, userData).pipe(
      tap((response: any) => {
        const token =
          response?.token ||
          response?.jwtToken ||
          response?.accessToken ||
          response?.jwt ||
          (typeof response === 'string' ? response : null);

        if (token) {
          localStorage.setItem(this.tokenKey, token);
        }
      })
    );
  }

  login(credentials: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, credentials).pipe(
      tap((response: any) => {
        const token =
          response?.token ||
          response?.jwtToken ||
          response?.accessToken ||
          response?.jwt ||
          (typeof response === 'string' ? response : null);

        if (token) {
          localStorage.setItem(this.tokenKey, token);
        }
      })
    );
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
  }
}
