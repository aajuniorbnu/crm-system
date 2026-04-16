import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./features/home/home-page.component').then((module) => module.HomePageComponent)
  },
  {
    path: 'painel',
    loadComponent: () =>
      import('./features/admin/admin-page.component').then((module) => module.AdminPageComponent)
  },
  {
    path: '**',
    redirectTo: ''
  }
];
