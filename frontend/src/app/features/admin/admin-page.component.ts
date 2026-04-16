import { CurrencyPipe } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Component, computed, DestroyRef, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import {
  CATEGORIAS_IMOVEL,
  CategoriaImovel,
  FINALIDADES_IMOVEL,
  FinalidadeImovel,
  formatEnumLabel,
  Imovel,
  STATUS_IMOVEL,
  StatusImovel
} from '../../core/models/imovel.model';
import { ImoveisApiService } from '../../core/services/imoveis-api.service';

@Component({
  selector: 'app-admin-page',
  imports: [ReactiveFormsModule, RouterLink, CurrencyPipe],
  templateUrl: './admin-page.component.html',
  styleUrl: './admin-page.component.scss'
})
export class AdminPageComponent {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly api = inject(ImoveisApiService);
  private readonly destroyRef = inject(DestroyRef);

  readonly categorias = CATEGORIAS_IMOVEL;
  readonly finalidades = FINALIDADES_IMOVEL;
  readonly statusOptions = STATUS_IMOVEL;
  readonly loading = signal(true);
  readonly saving = signal(false);
  readonly error = signal('');
  readonly success = signal('');
  readonly editingId = signal<number | null>(null);
  readonly imoveis = signal<Imovel[]>([]);

  readonly form = this.fb.group({
    codigo: ['IMO-001', [Validators.required, Validators.minLength(4)]],
    titulo: ['Apartamento moderno com varanda', [Validators.required, Validators.minLength(5)]],
    descricao: [
      'Descricao comercial do imovel com destaques de localizacao, planta e estilo de vida.',
      [Validators.required, Validators.minLength(20)]
    ],
    categoria: this.fb.control<CategoriaImovel>('APARTAMENTO', Validators.required),
    finalidade: this.fb.control<FinalidadeImovel>('VENDA', Validators.required),
    status: this.fb.control<StatusImovel>('DISPONIVEL', Validators.required),
    cidade: ['Sao Paulo', Validators.required],
    bairro: ['Moema', Validators.required],
    endereco: ['Avenida Jurucce, 1200', Validators.required],
    estado: ['SP', [Validators.required, Validators.maxLength(2)]],
    cep: ['04512-000', Validators.required],
    areaM2: [82, [Validators.required, Validators.min(10)]],
    quartos: [2, [Validators.required, Validators.min(0)]],
    banheiros: [2, [Validators.required, Validators.min(0)]],
    vagas: [1, [Validators.required, Validators.min(0)]],
    valor: [980000, [Validators.required, Validators.min(1000)]],
    condominio: [890, [Validators.required, Validators.min(0)]],
    iptu: [240, [Validators.required, Validators.min(0)]],
    destaque: [true],
    imagemUrl: ['']
  });

  readonly resumo = computed(() => ({
    venda: this.imoveis().filter((item) => item.finalidade === 'VENDA').length,
    aluguel: this.imoveis().filter((item) => item.finalidade === 'ALUGUEL').length,
    destaque: this.imoveis().filter((item) => item.destaque).length
  }));

  constructor() {
    this.carregarImoveis();
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
    const request = this.editingId()
      ? this.api.atualizar(this.editingId()!, payload)
      : this.api.criar(payload);

    request
      .pipe(
        finalize(() => this.saving.set(false)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: () => {
          this.success.set(
            this.editingId() ? 'Imovel atualizado com sucesso.' : 'Imovel cadastrado com sucesso.'
          );
          this.error.set('');
          this.novoRegistro();
          this.carregarImoveis();
        },
        error: () => {
          this.error.set(
            'Nao foi possivel salvar. Confirme se o backend Spring esta rodando em localhost:8080.'
          );
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
    if (!item.id) {
      return;
    }

    const confirmed = window.confirm(`Deseja remover o imovel ${item.codigo}?`);
    if (!confirmed) {
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
          this.carregarImoveis();
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
      codigo: 'IMO-001',
      titulo: 'Apartamento moderno com varanda',
      descricao:
        'Descricao comercial do imovel com destaques de localizacao, planta e estilo de vida.',
      categoria: 'APARTAMENTO',
      finalidade: 'VENDA',
      status: 'DISPONIVEL',
      cidade: 'Sao Paulo',
      bairro: 'Moema',
      endereco: 'Avenida Jurucce, 1200',
      estado: 'SP',
      cep: '04512-000',
      areaM2: 82,
      quartos: 2,
      banheiros: 2,
      vagas: 1,
      valor: 980000,
      condominio: 890,
      iptu: 240,
      destaque: true,
      imagemUrl: ''
    });
  }

  trackByCodigo(_: number, item: Imovel): string {
    return item.codigo;
  }

  controlHasError(controlName: keyof typeof this.form.controls): boolean {
    const control = this.form.controls[controlName];
    return control.invalid && (control.dirty || control.touched);
  }

  protected readonly formatEnumLabel = formatEnumLabel;

  private carregarImoveis(): void {
    this.loading.set(true);
    this.error.set('');

    this.api
      .listar()
      .pipe(
        finalize(() => this.loading.set(false)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (items) => this.imoveis.set(items),
        error: () => {
          this.imoveis.set([]);
          this.error.set(
            'Nao foi possivel carregar a lista. Verifique a API Spring antes de usar o painel.'
          );
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
