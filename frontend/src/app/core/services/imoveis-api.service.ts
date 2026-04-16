import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import {
  Imovel,
  ImovelFilters,
  IndicadoresImoveis
} from '../models/imovel.model';

@Injectable({ providedIn: 'root' })
export class ImoveisApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/imoveis';

  listar(filters: ImovelFilters = {}) {
    let params = new HttpParams();

    Object.entries(filters).forEach(([key, value]) => {
      if (value !== undefined && value !== null && `${value}`.trim() !== '') {
        params = params.set(key, `${value}`);
      }
    });

    return this.http.get<Imovel[]>(this.baseUrl, { params });
  }

  indicadores() {
    return this.http.get<IndicadoresImoveis>(`${this.baseUrl}/indicadores`);
  }

  criar(payload: Imovel) {
    return this.http.post<Imovel>(this.baseUrl, payload);
  }

  atualizar(id: number, payload: Imovel) {
    return this.http.put<Imovel>(`${this.baseUrl}/${id}`, payload);
  }

  remover(id: number) {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
