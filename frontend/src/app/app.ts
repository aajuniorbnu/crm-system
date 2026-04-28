import { Component, computed, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly corretor = this.authService.corretor;
  readonly isAuthenticated = this.authService.isAuthenticated;

  readonly navigation = [
    { label: 'Apartamento', href: '/imoveis/apartamento', exact: true },
    { label: 'Casa', href: '/imoveis/casa', exact: true },
    { label: 'Comercial', href: '/imoveis/comercial', exact: true },
    { label: 'Rural', href: '/imoveis/rural', exact: true }
  ];

  readonly authLink = computed(() =>
    this.isAuthenticated()
      ? { label: 'Painel', href: '/painel' }
      : { label: 'Login corretor', href: '/login' }
  );

  logout(): void {
    this.authService.logout();
    void this.router.navigateByUrl('/login');
  }
}
