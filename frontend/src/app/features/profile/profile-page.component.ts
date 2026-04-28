import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Component, DestroyRef, ElementRef, ViewChild, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { CorretorApiService } from '../../core/services/corretor-api.service';

@Component({
  selector: 'app-profile-page',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './profile-page.component.html'
})
export class ProfilePageComponent {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly corretorApi = inject(CorretorApiService);
  private readonly authService = inject(AuthService);
  private readonly destroyRef = inject(DestroyRef);

  @ViewChild('fotoInput') fotoInput?: ElementRef<HTMLInputElement>;

  readonly loading = signal(false);
  readonly saving = signal(false);
  readonly error = signal('');
  readonly success = signal('');
  readonly previewUrl = signal<string | null>(this.authService.corretor()?.fotoUrl ?? null);

  readonly form = this.fb.group({
    nome: ['', [Validators.required, Validators.minLength(3)]],
    telefone: ['', [Validators.required, Validators.minLength(8)]],
    creci: ['', [Validators.required, Validators.minLength(5)]]
  });

  constructor() {
    this.carregarPerfil();
  }

  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.error.set('Revise os campos obrigatorios do perfil.');
      return;
    }

    const payload = new FormData();
    const raw = this.form.getRawValue();
    payload.append('nome', raw.nome);
    payload.append('telefone', raw.telefone);
    payload.append('creci', raw.creci);

    const file = this.fotoInput?.nativeElement.files?.item(0);
    if (file) {
      payload.append('foto', file);
    }

    this.saving.set(true);
    this.error.set('');
    this.success.set('');

    this.corretorApi
      .atualizarPerfil(payload)
      .pipe(
        finalize(() => this.saving.set(false)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (corretor) => {
          this.authService.updateCorretor(corretor);
          this.previewUrl.set(corretor.fotoUrl);
          this.success.set('Perfil atualizado com sucesso.');
        },
        error: () => {
          this.error.set('Nao foi possivel atualizar o perfil do corretor.');
        }
      });
  }

  onFileChange(): void {
    const file = this.fotoInput?.nativeElement.files?.item(0);
    if (!file) {
      return;
    }

    const reader = new FileReader();
    reader.onload = () => this.previewUrl.set(typeof reader.result === 'string' ? reader.result : null);
    reader.readAsDataURL(file);
  }

  private carregarPerfil(): void {
    this.loading.set(true);

    this.corretorApi
      .perfil()
      .pipe(
        finalize(() => this.loading.set(false)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (corretor) => {
          this.form.reset({
            nome: corretor.nome,
            telefone: corretor.telefone,
            creci: corretor.creci
          });
          this.previewUrl.set(corretor.fotoUrl);
          this.authService.updateCorretor(corretor);
        },
        error: () => {
          this.error.set('Nao foi possivel carregar o perfil do corretor.');
        }
      });
  }
}
