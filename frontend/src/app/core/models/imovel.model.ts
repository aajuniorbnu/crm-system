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

export type ImovelTipoRoute = 'rural' | 'casa' | 'apartamento' | 'comercial';

export interface CorretorResumo {
  id: number;
  nome: string;
  email: string;
  telefone: string;
  creci: string;
  fotoUrl: string | null;
  ativo: boolean;
  dataCadastro: string;
}

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
  corretor?: CorretorResumo | null;
}

export interface ImovelFilters {
  tipo?: ImovelTipoRoute;
  finalidade?: FinalidadeImovel;
  categoria?: CategoriaImovel;
  status?: StatusImovel;
  cidade?: string;
  destaque?: boolean;
  precoMin?: number | null;
  precoMax?: number | null;
  areaMin?: number | null;
  areaMax?: number | null;
}

export interface IndicadoresImoveis {
  totalImoveis: number;
  imoveisVendaDisponiveis: number;
  imoveisAluguelDisponiveis: number;
  mediaVendaMetroQuadrado: number;
  mediaAluguelMetroQuadrado: number;
}

export interface DashboardCorretorResponse {
  corretor: CorretorResumo;
  imoveis: Imovel[];
  totalImoveis: number;
  destaques: number;
  valorPortfolio: number;
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

export function mapTipoToCategoria(tipo: ImovelTipoRoute): CategoriaImovel {
  const map: Record<ImovelTipoRoute, CategoriaImovel> = {
    apartamento: 'APARTAMENTO',
    casa: 'CASA',
    comercial: 'COMERCIAL',
    rural: 'RURAL'
  };

  return map[tipo];
}
