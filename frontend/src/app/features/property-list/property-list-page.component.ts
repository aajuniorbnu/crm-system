import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Component, DestroyRef, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import {
  Imovel,
  ImovelFilters,
  ImovelTipoRoute,
  mapTipoToCategoria
} from '../../core/models/imovel.model';
import { ImoveisApiService } from '../../core/services/imoveis-api.service';
import { PropertyCardComponent } from '../../shared/components/property-card/property-card.component';

type PageContent = {
  tipo: ImovelTipoRoute;
  titulo: string;
  subtitulo: string;
};

@Component({
  selector: 'app-property-list-page',
  imports: [FormsModule, RouterLink, PropertyCardComponent],
  templateUrl: './property-list-page.component.html'
})
export class PropertyListPageComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly api = inject(ImoveisApiService);
  private readonly destroyRef = inject(DestroyRef);

  readonly loading = signal(true);
  readonly error = signal('');
  readonly tipoAtual = signal<ImovelTipoRoute>('apartamento');
  readonly titulo = signal('');
  readonly subtitulo = signal('');
  readonly imoveis = signal<Imovel[]>([]);

  readonly filtros = signal<{
    cidade: string;
    precoMin: number | null;
    precoMax: number | null;
    areaMin: number | null;
    areaMax: number | null;
  }>({
    cidade: '',
    precoMin: null,
    precoMax: null,
    areaMin: null,
    areaMax: null
  });

  constructor() {
    this.route.data.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((data) => {
      const page = data as Partial<PageContent>;
      if (!page.tipo || !page.titulo || !page.subtitulo) {
        return;
      }

      this.tipoAtual.set(page.tipo);
      this.titulo.set(page.titulo);
      this.subtitulo.set(page.subtitulo);
      this.carregarImoveis();
    });
  }

  atualizarCampo(
    field: 'cidade' | 'precoMin' | 'precoMax' | 'areaMin' | 'areaMax',
    value: string | number | null
  ): void {
    this.filtros.update((state) => ({
      ...state,
      [field]:
        field === 'cidade'
          ? String(value)
          : value === '' || value === null
            ? null
            : Number(value)
    }));
  }

  aplicarFiltros(): void {
    this.carregarImoveis();
  }

  limparFiltros(): void {
    this.filtros.set({
      cidade: '',
      precoMin: null,
      precoMax: null,
      areaMin: null,
      areaMax: null
    });
    this.carregarImoveis();
  }

  private carregarImoveis(): void {
    const filtros = this.filtros();
    const query: ImovelFilters = {
      tipo: this.tipoAtual(),
      categoria: mapTipoToCategoria(this.tipoAtual()),
      status: 'DISPONIVEL',
      cidade: filtros.cidade.trim(),
      precoMin: filtros.precoMin,
      precoMax: filtros.precoMax,
      areaMin: filtros.areaMin,
      areaMax: filtros.areaMax
    };

    this.loading.set(true);
    this.error.set('');

    this.api
      .listar(query)
      .pipe(
        finalize(() => this.loading.set(false)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (imoveis) => this.imoveis.set(imoveis),
        error: () => {
          this.imoveis.set([]);
          this.error.set('Nao foi possivel carregar os imoveis agora. Verifique a API Spring Boot.');
        }
      });
  }
}
