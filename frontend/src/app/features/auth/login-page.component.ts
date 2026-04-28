import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Component, DestroyRef, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login-page',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login-page.component.html'
})
export class LoginPageComponent {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  readonly loading = signal(false);
  readonly error = signal('');

  readonly form = this.fb.group({
    email: ['paula@vistaprime.com', [Validators.required, Validators.email]],
    senha: ['paula123', [Validators.required, Validators.minLength(6)]]
  });

  entrar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.error.set('Informe email e senha validos.');
      return;
    }

    this.loading.set(true);
    this.error.set('');

    this.authService
      .login(this.form.getRawValue())
      .pipe(
        finalize(() => this.loading.set(false)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: () => {
          void this.router.navigateByUrl('/painel');
        },
        error: () => {
          this.error.set('Credenciais invalidas. Use um corretor seed para testar o JWT.');
        }
      });
  }
}
