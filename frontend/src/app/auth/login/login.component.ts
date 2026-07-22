/**
 * GUIDA DIDATTICA DEL FILE
 * ---------------------------------------------------------------------------
 * Logica della pagina di login cliente/staff con Reactive Forms.
 * Leggi prima gli import, poi @Component/@Injectable e infine campi e metodi:
 * questa è la stessa sequenza con cui Angular compone il comportamento del file.
 */
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { LoginArea } from '../../core/models/auth.model';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, MatButtonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  readonly loginForm = this.fb.nonNullable.group({
    area: ['CLIENTE' as LoginArea, Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  readonly isSubmitting = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly showPassword = signal(false);

  /** BLOCCO DIDATTICO — Mostra o nasconde la password senza modificarla. */
  togglePassword(): void {
    this.showPassword.update((value) => !value);
  }

  /** BLOCCO DIDATTICO — Valida le credenziali, chiama AuthService e gestisce il redirect dopo il login. */
  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    this.errorMessage.set(null);
    const { area, email, password } = this.loginForm.getRawValue();

    this.authService.login({ email, password }, area).subscribe({
      next: (response) => {
        this.isSubmitting.set(false);
        const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl');
        this.router.navigateByUrl(
          returnUrl ?? (response.user.role === 'STAFF' ? '/staff' : '/area-personale')
        );
      },
      error: () => {
        this.isSubmitting.set(false);
        this.errorMessage.set(
          area === 'STAFF' ? 'Credenziali staff non corrette.' : 'Email o password non corrette.'
        );
      }
    });
  }
}
