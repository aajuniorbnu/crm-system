import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';
import { ImovelTipoRoute } from './core/models/imovel.model';

type PropertyRouteData = {
  tipo: ImovelTipoRoute;
  titulo: string;
  subtitulo: string;
};

const propertyRoute = (data: PropertyRouteData) => ({
  path: `imoveis/${data.tipo}`,
  loadComponent: () =>
    import('./features/property-list/property-list-page.component').then(
      (module) => module.PropertyListPageComponent
    ),
  data
});

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'imoveis/apartamento'
  },
  propertyRoute({
    tipo: 'apartamento',
    titulo: 'Apartamentos com curadoria digital',
    subtitulo: 'Unidades urbanas com leitura rapida de preco, area e localizacao.'
  }),
  propertyRoute({
    tipo: 'casa',
    titulo: 'Casas para morar e investir',
    subtitulo: 'Portifolio residencial com filtros por cidade, faixa de valor e metragem.'
  }),
  propertyRoute({
    tipo: 'comercial',
    titulo: 'Imoveis comerciais em polos ativos',
    subtitulo: 'Salas, conjuntos e espacos corporativos prontos para operacao.'
  }),
  propertyRoute({
    tipo: 'rural',
    titulo: 'Propriedades rurais com escala',
    subtitulo: 'Sitios e areas produtivas para lazer, operacao e valorizacao de longo prazo.'
  }),
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login-page.component').then((module) => module.LoginPageComponent)
  },
  {
    path: 'painel',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/admin/admin-page.component').then((module) => module.AdminPageComponent)
  },
  {
    path: 'painel/perfil',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/profile/profile-page.component').then((module) => module.ProfilePageComponent)
  },
  {
    path: '**',
    redirectTo: 'imoveis/apartamento'
  }
];
