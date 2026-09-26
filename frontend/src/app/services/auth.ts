import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';

export interface LoginRequestDTO {
  usernameOrEmail: string;
  password?: string;
}

export interface RegisterRequestDTO {
  username: string;
  email: string;
  password?: string;
  fullName: string;
}

export interface AuthResponseDTO {
  token: string;
  tokenType: string;
  userId: number;
  username: string;
  email: string;
}

export interface UserProfile {
  userId: number;
  username: string;
  email: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly apiUrl = '/api/auth';
  private readonly tokenKey = 'jwt_token';
  private readonly userKey = 'user';

  private authState$ = new BehaviorSubject<boolean>(this.isLoggedIn());
  public isLoggedIn$ = this.authState$.asObservable();

  constructor(private http: HttpClient) {}

  register(userData: RegisterRequestDTO): Observable<AuthResponseDTO> {
    return this.http.post<AuthResponseDTO>(`${this.apiUrl}/register`, userData).pipe(
      tap((response: AuthResponseDTO) => this.handleSessionStorage(response))
    );
  }

  login(credentials: LoginRequestDTO): Observable<AuthResponseDTO> {
    return this.http.post<AuthResponseDTO>(`${this.apiUrl}/login`, credentials).pipe(
      tap((response: AuthResponseDTO) => this.handleSessionStorage(response))
    );
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  getUser(): UserProfile | null {
    const userData = localStorage.getItem(this.userKey);
    return userData ? JSON.parse(userData) : null;
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem('token');
    localStorage.removeItem(this.userKey);
    this.authState$.next(false);
  }

  private handleSessionStorage(response: AuthResponseDTO): void {
    if (response?.token) {
      localStorage.removeItem('token');
      localStorage.setItem(this.tokenKey, response.token);

      const userProfile: UserProfile = {
        userId: response.userId,
        username: response.username,
        email: response.email
      };
      localStorage.setItem(this.userKey, JSON.stringify(userProfile));
      this.authState$.next(true);
    }
  }
}
