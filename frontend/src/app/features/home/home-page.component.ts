import { CurrencyPipe } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Component, computed, DestroyRef, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import {
  CATEGORIAS_IMOVEL,
  CategoriaImovel,
  EMPTY_INDICADORES,
  FinalidadeImovel,
  formatEnumLabel,
  Imovel,
  IndicadoresImoveis
} from '../../core/models/imovel.model';
import { ImoveisApiService } from '../../core/services/imoveis-api.service';
import { PropertyCardComponent } from '../../shared/components/property-card/property-card.component';

@Component({
  selector: 'app-home-page',
  imports: [FormsModule, RouterLink, CurrencyPipe, PropertyCardComponent],
  templateUrl: './home-page.component.html',
  styleUrl: './home-page.component.scss'
})
export class HomePageComponent {
  private readonly api = inject(ImoveisApiService);
  private readonly destroyRef = inject(DestroyRef);

  readonly categorias = CATEGORIAS_IMOVEL;
  readonly loading = signal(true);
  readonly error = signal('');
  readonly imoveis = signal<Imovel[]>([]);
  readonly indicadores = signal<IndicadoresImoveis>(EMPTY_INDICADORES);
  readonly finalidadeAtiva = signal<'TODOS' | FinalidadeImovel>('TODOS');
  readonly categoriaAtiva = signal<'TODAS' | CategoriaImovel>('TODAS');
  readonly cidadeBusca = signal('');
  readonly calculadoraArea = signal(95);
  readonly calculadoraValor = signal(890000);
  readonly calculadoraFinalidade = signal<FinalidadeImovel>('VENDA');

  readonly destaques = computed(() => this.imoveis().filter((item) => item.destaque).slice(0, 3));

  readonly imoveisFiltrados = computed(() => {
    const cidade = this.cidadeBusca().trim().toLowerCase();

    return this.imoveis().filter((item) => {
      const matchesFinalidade =
        this.finalidadeAtiva() === 'TODOS' || item.finalidade === this.finalidadeAtiva();
      const matchesCategoria =
        this.categoriaAtiva() === 'TODAS' || item.categoria === this.categoriaAtiva();
      const matchesCidade =
        !cidade ||
        item.cidade.toLowerCase().includes(cidade) ||
        item.bairro.toLowerCase().includes(cidade);

      return matchesFinalidade && matchesCategoria && matchesCidade;
    });
  });

  readonly valorCalculadoMetroQuadrado = computed(() => {
    const area = this.calculadoraArea();
    const valor = this.calculadoraValor();

    if (!area || area <= 0 || !valor || valor <= 0) {
      return 0;
    }

    return valor / area;
  });

  readonly mediaMercadoSelecionada = computed(() =>
    this.calculadoraFinalidade() === 'VENDA'
      ? this.indicadores().mediaVendaMetroQuadrado
      : this.indicadores().mediaAluguelMetroQuadrado
  );

  constructor() {
    this.carregarDashboard();
  }

  definirFinalidade(value: 'TODOS' | FinalidadeImovel): void {
    this.finalidadeAtiva.set(value);
  }

  definirCategoria(value: 'TODAS' | CategoriaImovel): void {
    this.categoriaAtiva.set(value);
  }

  atualizarCidade(value: string): void {
    this.cidadeBusca.set(value);
  }

  atualizarCalculadoraArea(value: number | string): void {
    this.calculadoraArea.set(Number(value) || 0);
  }

  atualizarCalculadoraValor(value: number | string): void {
    this.calculadoraValor.set(Number(value) || 0);
  }

  selecionarCalculadora(value: FinalidadeImovel): void {
    this.calculadoraFinalidade.set(value);
  }

  trackByCodigo(_: number, item: Imovel): string {
    return item.codigo;
  }

  protected readonly formatEnumLabel = formatEnumLabel;

  private carregarDashboard(): void {
    this.loading.set(true);
    this.error.set('');

    forkJoin({
      imoveis: this.api.listar({ status: 'DISPONIVEL' }),
      indicadores: this.api.indicadores()
    })
      .pipe(
        catchError(() => {
          this.error.set(
            'Nao foi possivel carregar os imoveis agora. Suba o backend Spring em localhost:8080 para integrar a API.'
          );

          return of({
            imoveis: [],
            indicadores: EMPTY_INDICADORES
          });
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(({ imoveis, indicadores }) => {
        this.imoveis.set(imoveis);
        this.indicadores.set(indicadores);
        this.loading.set(false);
      });
  }
}
