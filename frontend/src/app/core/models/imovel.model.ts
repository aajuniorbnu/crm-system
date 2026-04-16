export type CategoriaImovel =
  | 'APARTAMENTO'
  | 'CASA'
  | 'COBERTURA'
  | 'TERRENO'
  | 'COMERCIAL'
  | 'RURAL';

export type FinalidadeImovel = 'VENDA' | 'ALUGUEL';

export type StatusImovel =
  | 'DISPONIVEL'
  | 'RESERVADO'
  | 'VENDIDO'
  | 'ALUGADO'
  | 'INATIVO';

export interface Imovel {
  id?: number;
  codigo: string;
  titulo: string;
  descricao: string;
  categoria: CategoriaImovel;
  finalidade: FinalidadeImovel;
  status: StatusImovel;
  cidade: string;
  bairro: string;
  endereco: string;
  estado: string;
  cep: string;
  areaM2: number;
  quartos: number;
  banheiros: number;
  vagas: number;
  valor: number;
  condominio: number;
  iptu: number;
  destaque: boolean;
  imagemUrl?: string | null;
  dataCadastro?: string;
}

export interface ImovelFilters {
  finalidade?: FinalidadeImovel;
  categoria?: CategoriaImovel;
  status?: StatusImovel;
  cidade?: string;
  destaque?: boolean;
}

export interface IndicadoresImoveis {
  totalImoveis: number;
  imoveisVendaDisponiveis: number;
  imoveisAluguelDisponiveis: number;
  mediaVendaMetroQuadrado: number;
  mediaAluguelMetroQuadrado: number;
}

export const CATEGORIAS_IMOVEL: CategoriaImovel[] = [
  'APARTAMENTO',
  'CASA',
  'COBERTURA',
  'TERRENO',
  'COMERCIAL',
  'RURAL'
];

export const FINALIDADES_IMOVEL: FinalidadeImovel[] = ['VENDA', 'ALUGUEL'];

export const STATUS_IMOVEL: StatusImovel[] = [
  'DISPONIVEL',
  'RESERVADO',
  'VENDIDO',
  'ALUGADO',
  'INATIVO'
];

export const EMPTY_INDICADORES: IndicadoresImoveis = {
  totalImoveis: 0,
  imoveisVendaDisponiveis: 0,
  imoveisAluguelDisponiveis: 0,
  mediaVendaMetroQuadrado: 0,
  mediaAluguelMetroQuadrado: 0
};

export function formatEnumLabel(value: string): string {
  return value
    .toLowerCase()
    .split('_')
    .map((chunk) => chunk.charAt(0).toUpperCase() + chunk.slice(1))
    .join(' ');
}

export function calculateValuePerSquareMeter(areaM2: number, valor: number): number {
  if (!areaM2 || areaM2 <= 0 || !valor || valor <= 0) {
    return 0;
  }

  return valor / areaM2;
}
