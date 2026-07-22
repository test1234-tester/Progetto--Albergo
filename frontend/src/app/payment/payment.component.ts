/**
 * GUIDA DIDATTICA DEL FILE
 * ---------------------------------------------------------------------------
 * Pagina di pagamento della caparra collegata a una prenotazione.
 * Leggi prima gli import, poi @Component/@Injectable e infine campi e metodi:
 * questa è la stessa sequenza con cui Angular compone il comportamento del file.
 */
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

  // =========================================================================
  // 1. VERSIONE TEMPORANEA (SIMULAZIONE FRONTEND ATTIVA)
  // =========================================================================
  console.warn("[MOCK] Elaborazione pagamento della caparra in corso...");
  
  setTimeout(() => {
    this.isSubmitting.set(false);
    
    // Cambiamo lo stato in 'CONFERMATA' o 'APPROVATO' per attivare l'HTML di successo!
    // Nota: adatta il nome del signal (es. confirmedStatus o statoPagamento) in base a come è dichiarato nel tuo TS
    this.confirmedStatus.set('PAGAMENTO_CAPARRA_ACQUISTATO'); 
    
    console.log("[MOCK] Pagamento completato con successo!");
  }, 1500); // Simula il check della banca per un secondo e mezzo

  // =========================================================================
  // 2. VERSIONE CORRETTA (COMUNICAZIONE BACKEND REALE - COMMENTATA)
  // =========================================================================
  /*
  this.paymentService.processPayment(this.paymentForm.getRawValue()).subscribe({
    next: (res) => {
      this.isSubmitting.set(false);
      this.confirmedStatus.set(res.status); // Riceve la conferma dal server
    },
    error: () => {
      this.isSubmitting.set(false);
      this.errorMessage.set('Pagamento non riuscito. Controlla i dati della carta e riprova.');
    }
  });
  */
}
  /** BLOCCO DIDATTICO — Riporta l’utente alla homepage al termine del flusso. */
  goHome(): void {
    this.router.navigate(['/']);
  }
}
