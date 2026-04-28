import { CurrencyPipe } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Component, computed, DestroyRef, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import {
  CATEGORIAS_IMOVEL,
  CategoriaImovel,
  DashboardCorretorResponse,
  FINALIDADES_IMOVEL,
  FinalidadeImovel,
  formatEnumLabel,
  Imovel,
  STATUS_IMOVEL,
  StatusImovel
} from '../../core/models/imovel.model';
import { AuthService } from '../../core/services/auth.service';
import { CorretorApiService } from '../../core/services/corretor-api.service';
import { ImoveisApiService } from '../../core/services/imoveis-api.service';

@Component({
  selector: 'app-admin-page',
  imports: [ReactiveFormsModule, RouterLink, CurrencyPipe],
  templateUrl: './admin-page.component.html'
})
export class AdminPageComponent {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly api = inject(ImoveisApiService);
  private readonly corretorApi = inject(CorretorApiService);
  private readonly authService = inject(AuthService);
  private readonly destroyRef = inject(DestroyRef);

  readonly categorias = CATEGORIAS_IMOVEL;
  readonly finalidades = FINALIDADES_IMOVEL;
  readonly statusOptions = STATUS_IMOVEL;
  readonly loading = signal(true);
  readonly saving = signal(false);
  readonly error = signal('');
  readonly success = signal('');
  readonly editingId = signal<number | null>(null);
  readonly dashboard = signal<DashboardCorretorResponse | null>(null);

  readonly form = this.fb.group({
    codigo: ['IMO-101', [Validators.required, Validators.minLength(4)]],
    titulo: ['Apartamento premium com vista livre', [Validators.required, Validators.minLength(5)]],
    descricao: [
      'Descricao comercial com beneficios de localizacao, distribuicao interna e potencial de negociacao.',
      [Validators.required, Validators.minLength(20)]
    ],
    categoria: this.fb.control<CategoriaImovel>('APARTAMENTO', Validators.required),
    finalidade: this.fb.control<FinalidadeImovel>('VENDA', Validators.required),
    status: this.fb.control<StatusImovel>('DISPONIVEL', Validators.required),
    cidade: ['Sao Paulo', Validators.required],
    bairro: ['Vila Mariana', Validators.required],
    endereco: ['Rua Modelo, 100', Validators.required],
    estado: ['SP', [Validators.required, Validators.maxLength(2)]],
    cep: ['04015-000', Validators.required],
    areaM2: [95, [Validators.required, Validators.min(10)]],
    quartos: [3, [Validators.required, Validators.min(0)]],
    banheiros: [2, [Validators.required, Validators.min(0)]],
    vagas: [2, [Validators.required, Validators.min(0)]],
    valor: [1250000, [Validators.required, Validators.min(1000)]],
    condominio: [980, [Validators.required, Validators.min(0)]],
    iptu: [260, [Validators.required, Validators.min(0)]],
    destaque: [true],
    imagemUrl: ['']
  });

  readonly imoveis = computed(() => this.dashboard()?.imoveis ?? []);
  readonly corretor = computed(() => this.dashboard()?.corretor ?? this.authService.corretor());

  constructor() {
    this.carregarDashboard();
  }

  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.error.set('Preencha os campos obrigatorios antes de salvar.');
      this.success.set('');
      return;
    }

    this.saving.set(true);
    this.error.set('');
    this.success.set('');

    const payload = this.buildPayload();
    const request = this.editingId() ? this.api.atualizar(this.editingId()!, payload) : this.api.criar(payload);

    request
      .pipe(
        finalize(() => this.saving.set(false)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: () => {
          this.success.set(this.editingId() ? 'Imovel atualizado com sucesso.' : 'Imovel cadastrado com sucesso.');
          this.error.set('');
          this.novoRegistro();
          this.carregarDashboard();
        },
        error: () => {
          this.error.set('Nao foi possivel salvar o imovel do corretor.');
          this.success.set('');
        }
      });
  }

  editar(item: Imovel): void {
    this.editingId.set(item.id ?? null);
    this.success.set('');
    this.error.set('');
    this.form.setValue({
      codigo: item.codigo,
      titulo: item.titulo,
      descricao: item.descricao,
      categoria: item.categoria,
      finalidade: item.finalidade,
      status: item.status,
      cidade: item.cidade,
      bairro: item.bairro,
      endereco: item.endereco,
      estado: item.estado,
      cep: item.cep,
      areaM2: item.areaM2,
      quartos: item.quartos,
      banheiros: item.banheiros,
      vagas: item.vagas,
      valor: item.valor,
      condominio: item.condominio,
      iptu: item.iptu,
      destaque: item.destaque,
      imagemUrl: item.imagemUrl ?? ''
    });
  }

  remover(item: Imovel): void {
    if (!item.id || !window.confirm(`Deseja remover o imovel ${item.codigo}?`)) {
      return;
    }

    this.api
      .remover(item.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.success.set('Imovel removido com sucesso.');
          this.error.set('');
          if (this.editingId() === item.id) {
            this.novoRegistro();
          }
          this.carregarDashboard();
        },
        error: () => {
          this.error.set('Nao foi possivel remover o imovel selecionado.');
          this.success.set('');
        }
      });
  }

  novoRegistro(): void {
    this.editingId.set(null);
    this.form.reset({
      codigo: 'IMO-101',
      titulo: 'Apartamento premium com vista livre',
      descricao:
        'Descricao comercial com beneficios de localizacao, distribuicao interna e potencial de negociacao.',
      categoria: 'APARTAMENTO',
      finalidade: 'VENDA',
      status: 'DISPONIVEL',
      cidade: 'Sao Paulo',
      bairro: 'Vila Mariana',
      endereco: 'Rua Modelo, 100',
      estado: 'SP',
      cep: '04015-000',
      areaM2: 95,
      quartos: 3,
      banheiros: 2,
      vagas: 2,
      valor: 1250000,
      condominio: 980,
      iptu: 260,
      destaque: true,
      imagemUrl: ''
    });
  }

  controlHasError(controlName: keyof typeof this.form.controls): boolean {
    const control = this.form.controls[controlName];
    return control.invalid && (control.dirty || control.touched);
  }

  protected readonly formatEnumLabel = formatEnumLabel;

  private carregarDashboard(): void {
    this.loading.set(true);
    this.error.set('');

    this.corretorApi
      .dashboard()
      .pipe(
        finalize(() => this.loading.set(false)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (dashboard) => {
          this.dashboard.set(dashboard);
          this.authService.updateCorretor(dashboard.corretor);
        },
        error: () => {
          this.dashboard.set(null);
          this.error.set('Nao foi possivel carregar o dashboard protegido do corretor.');
        }
      });
  }

  private buildPayload(): Imovel {
    const raw = this.form.getRawValue();

    return {
      codigo: raw.codigo.trim().toUpperCase(),
      titulo: raw.titulo.trim(),
      descricao: raw.descricao.trim(),
      categoria: raw.categoria,
      finalidade: raw.finalidade,
      status: raw.status,
      cidade: raw.cidade.trim(),
      bairro: raw.bairro.trim(),
      endereco: raw.endereco.trim(),
      estado: raw.estado.trim().toUpperCase(),
      cep: raw.cep.trim(),
      areaM2: Number(raw.areaM2),
      quartos: Number(raw.quartos),
      banheiros: Number(raw.banheiros),
      vagas: Number(raw.vagas),
      valor: Number(raw.valor),
      condominio: Number(raw.condominio),
      iptu: Number(raw.iptu),
      destaque: raw.destaque,
      imagemUrl: raw.imagemUrl.trim() || null
    };
  }
}
