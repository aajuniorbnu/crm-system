import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { DashboardCorretorResponse, CorretorResumo } from '../models/imovel.model';

@Injectable({ providedIn: 'root' })
export class CorretorApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/corretores/me';

  perfil() {
    return this.http.get<CorretorResumo>(this.baseUrl);
  }

  dashboard() {
    return this.http.get<DashboardCorretorResponse>(`${this.baseUrl}/dashboard`);
  }

  atualizarPerfil(payload: FormData) {
    return this.http.put<CorretorResumo>(`${this.baseUrl}/perfil`, payload);
  }
}
