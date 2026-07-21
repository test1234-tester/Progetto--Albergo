import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';

import { environment } from '../../../environments/environment';
import {
  AuthResponse,
  LoginArea,
  LoginRequest,
  RegisterRequest,
  User
} from '../models/auth.model';

const TOKEN_KEY = 'albergo_token';
const USER_KEY = 'albergo_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly baseUrl = `${environment.apiUrl}/auth`;

  private readonly _currentUser = signal<User | null>(this.readValidStoredUser());
  readonly currentUser = this._currentUser.asReadonly();
  readonly isAuthenticated = computed(
    () => this._currentUser() !== null && localStorage.getItem(TOKEN_KEY) !== null
  );
  readonly isStaff = computed(() => this._currentUser()?.role === 'STAFF');
  readonly isCustomer = computed(() => this._currentUser()?.role === 'CLIENTE');

  login(payload: LoginRequest, area: LoginArea = 'CLIENTE'): Observable<AuthResponse> {
    const endpoint = area === 'STAFF' ? `${this.baseUrl}/staff/login` : `${this.baseUrl}/login`;
    return this.http
      .post<AuthResponse>(endpoint, {
        email: payload.email.trim().toLowerCase(),
        password: payload.password
      })
      .pipe(tap((response) => this.setSession(response)));
  }

  register(payload: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.baseUrl}/register`, {
        ...payload,
        email: payload.email.trim().toLowerCase()
      })
      .pipe(tap((response) => this.setSession(response)));
  }

  updateCurrentUser(changes: Partial<User>): void {
    const current = this._currentUser();
    if (!current) return;
    const updated = { ...current, ...changes };
    localStorage.setItem(USER_KEY, JSON.stringify(updated));
    this._currentUser.set(updated);
  }

  logout(): void {
    this.clearSession();
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    const token = localStorage.getItem(TOKEN_KEY);
    if (!token || this.isTokenExpired(token)) {
      this.clearSession();
      return null;
    }
    return token;
  }

  private setSession(response: AuthResponse): void {
    localStorage.setItem(TOKEN_KEY, response.token);
    localStorage.setItem(USER_KEY, JSON.stringify(response.user));
    this._currentUser.set(response.user);
  }

  private readValidStoredUser(): User | null {
    const token = localStorage.getItem(TOKEN_KEY);
    const rawUser = localStorage.getItem(USER_KEY);
    if (!token || !rawUser || this.isTokenExpired(token)) {
      localStorage.removeItem(TOKEN_KEY);
      localStorage.removeItem(USER_KEY);
      return null;
    }

    try {
      const user = JSON.parse(rawUser) as User;
      if (user.role !== 'CLIENTE' && user.role !== 'STAFF') {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(USER_KEY);
        return null;
      }
      return user;
    } catch {
      localStorage.removeItem(TOKEN_KEY);
      localStorage.removeItem(USER_KEY);
      return null;
    }
  }

  private isTokenExpired(token: string): boolean {
    try {
      const payloadPart = token.split('.')[1];
      const normalized = payloadPart.replace(/-/g, '+').replace(/_/g, '/');
      const padded = normalized.padEnd(Math.ceil(normalized.length / 4) * 4, '=');
      const payload = JSON.parse(atob(padded));
      return typeof payload.exp !== 'number' || payload.exp * 1000 <= Date.now();
    } catch {
      return true;
    }
  }

  private clearSession(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this._currentUser.set(null);
  }
}
