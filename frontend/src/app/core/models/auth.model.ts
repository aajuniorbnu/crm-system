import { CorretorResumo } from './imovel.model';

export interface LoginPayload {
  email: string;
  senha: string;
}

export interface AuthResponse {
  token: string;
  corretor: CorretorResumo;
}
