import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { tap } from 'rxjs';
import { AuthResponse, LoginPayload } from '../models/auth.model';
import { CorretorResumo } from '../models/imovel.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly tokenKey = 'crm.auth.token';
  private readonly corretorKey = 'crm.auth.corretor';

  private readonly tokenState = signal<string | null>(localStorage.getItem(this.tokenKey));
  private readonly corretorState = signal<CorretorResumo | null>(this.readStoredCorretor());

  readonly token = this.tokenState.asReadonly();
  readonly corretor = this.corretorState.asReadonly();
  readonly isAuthenticated = computed(() => !!this.tokenState());

  login(payload: LoginPayload) {
    return this.http.post<AuthResponse>('/api/auth/login', payload).pipe(
      tap((response) => this.persistSession(response))
    );
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.corretorKey);
    this.tokenState.set(null);
    this.corretorState.set(null);
  }

  updateCorretor(corretor: CorretorResumo): void {
    localStorage.setItem(this.corretorKey, JSON.stringify(corretor));
    this.corretorState.set(corretor);
  }

  getToken(): string | null {
    return this.tokenState();
  }

  private persistSession(response: AuthResponse): void {
    localStorage.setItem(this.tokenKey, response.token);
    localStorage.setItem(this.corretorKey, JSON.stringify(response.corretor));
    this.tokenState.set(response.token);
    this.corretorState.set(response.corretor);
  }

  private readStoredCorretor(): CorretorResumo | null {
    const raw = localStorage.getItem(this.corretorKey);
    if (!raw) {
      return null;
    }

    try {
      return JSON.parse(raw) as CorretorResumo;
    } catch {
      localStorage.removeItem(this.corretorKey);
      return null;
    }
  }
}
