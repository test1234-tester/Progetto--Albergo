/**
 * GUIDA DIDATTICA DEL FILE
 * ---------------------------------------------------------------------------
 * Logica della registrazione cliente e validazione del form.
 * Leggi prima gli import, poi @Component/@Injectable e infine campi e metodi:
 * questa è la stessa sequenza con cui Angular compone il comportamento del file.
 */
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../core/services/auth.service';

function passwordsMatchValidator(): ValidatorFn {
  return (group: AbstractControl): ValidationErrors | null =>
    group.get('password')?.value === group.get('confirmPassword')?.value
      ? null
      : { passwordsMismatch: true };
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, MatButtonModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly isSubmitting = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly showPassword = signal(false);
  readonly showConfirmPassword = signal(false);

  /** BLOCCO DIDATTICO — Mostra o nasconde il campo password. */
  togglePassword(): void {
    this.showPassword.update((value) => !value);
  }

  /** BLOCCO DIDATTICO — Mostra o nasconde la conferma password. */
  toggleConfirmPassword(): void {
    this.showConfirmPassword.update((value) => !value);
  }

  readonly registerForm = this.fb.nonNullable.group(
    {
      nome: ['', [Validators.required, Validators.minLength(2)]],
      cognome: ['', [Validators.required, Validators.minLength(2)]],
      username: ['', [Validators.required, Validators.minLength(3)]],
      cellulare: ['', [Validators.required, Validators.pattern(/^[0-9+ ()-]{6,20}$/)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required]
    },
    { validators: passwordsMatchValidator() }
  );

  /** BLOCCO DIDATTICO — Valida il form, verifica la corrispondenza password e registra il cliente. */
  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    this.errorMessage.set(null);
    const { confirmPassword: _confirmPassword, ...payload } = this.registerForm.getRawValue();

    this.authService.register(payload).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.router.navigate(['/area-personale']);
      },
      error: (error: HttpErrorResponse) => {
        this.isSubmitting.set(false);
        this.applyServerErrors(error);
        this.errorMessage.set(
          error.status === 409
            ? 'Questa email è già registrata.'
            : error.status === 400
              ? 'Controlla i campi evidenziati in rosso.'
              : 'Registrazione non riuscita. Controlla i dati o la connessione.'
        );
      }
    });
  }
  private applyServerErrors(error: HttpErrorResponse): void {
    const message = String(error.error?.message ?? error.error?.detail ?? error.message ?? '').toLowerCase();

    if (error.status === 409 || message.includes('email')) {
      this.registerForm.controls.email.setErrors({
        ...(this.registerForm.controls.email.errors ?? {}),
        server: true
      });
      this.registerForm.controls.email.markAsTouched();
    }

    const fields: Array<keyof typeof this.registerForm.controls> = [
      'nome', 'cognome', 'username', 'cellulare', 'email', 'password'
    ];
    for (const field of fields) {
      if (message.includes(field)) {
        const control = this.registerForm.controls[field];
        control.setErrors({ ...(control.errors ?? {}), server: true });
        control.markAsTouched();
      }
    }
  }

}
