import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  readonly navigation = [
    { label: 'Catalogo', href: '/', exact: true },
    { label: 'Painel', href: '/painel', exact: false }
  ];
}
