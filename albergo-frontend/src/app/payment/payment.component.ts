import { Component, computed, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PaymentService } from '../core/services/payment.service';

const CAPARRA_RATE = 0.1; // 10% caparra sull'importo dell'albergo (regola confermata da Sviluppatore 1)

@Component({
  selector: 'app-payment',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './payment.component.html',
  styleUrl: './payment.component.scss'
})
export class PaymentComponent {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly paymentService = inject(PaymentService);

  private readonly bookingId = Number(this.route.snapshot.paramMap.get('id'));
  // Importo calcolato nel form di prenotazione (PricingService): qui solo per il riepilogo,
  // la caparra effettiva viene ricalcolata e validata dal backend
  private readonly totalAmount = Number(this.route.snapshot.queryParamMap.get('totalAmount') ?? 0);

  readonly caparra = computed(() => Math.round(this.totalAmount * CAPARRA_RATE * 100) / 100);
  readonly saldo = computed(() => Math.round((this.totalAmount - this.caparra()) * 100) / 100);

  readonly isSubmitting = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly confirmedStatus = signal<string | null>(null);

  readonly paymentForm = this.fb.nonNullable.group({
    cardHolder: ['', Validators.required],
    cardNumber: ['', [Validators.required, Validators.pattern(/^\d{16}$/)]],
    expiry: ['', [Validators.required, Validators.pattern(/^(0[1-9]|1[0-2])\/\d{2}$/)]],
    cvv: ['', [Validators.required, Validators.pattern(/^\d{3,4}$/)]]
  });

  onSubmit(): void {
    if (this.paymentForm.invalid) {
      this.paymentForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    this.errorMessage.set(null);

    this.paymentService
      .pay({ bookingId: this.bookingId, ...this.paymentForm.getRawValue() })
      .subscribe({
        next: (res) => {
          this.isSubmitting.set(false);
          // Lo stato torna sempre "in attesa conferma": nessuna conferma istantanea lato client
          this.confirmedStatus.set(res.stato);
        },
        error: (err: HttpErrorResponse) => {
          this.isSubmitting.set(false);
          this.errorMessage.set('Pagamento non riuscito. Controlla i dati della carta e riprova.');
        }
      });
  }

  goHome(): void {
    this.router.navigate(['/']);
  }
}
