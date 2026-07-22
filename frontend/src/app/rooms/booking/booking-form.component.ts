/**
 * GUIDA DIDATTICA DEL FILE
 * ---------------------------------------------------------------------------
 * Form di prenotazione dedicato a una camera, per utente autenticato o ospite anonimo.
 * Leggi prima gli import, poi @Component/@Injectable e infine campi e metodi:
 * questa è la stessa sequenza con cui Angular compone il comportamento del file.
 */
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { AbstractControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';

import { Room } from '../../core/models/room.model';
import { AuthService } from '../../core/services/auth.service';
import { BookingService } from '../../core/services/booking.service';
import { PricingService } from '../../core/services/pricing.service';
import { RoomService } from '../../core/services/room.service';

@Component({
  selector: 'app-booking-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, MatButtonModule],
  templateUrl: './booking-form.component.html',
  styleUrl: './booking-form.component.scss'
})
export class BookingFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly roomService = inject(RoomService);
  private readonly bookingService = inject(BookingService);
  private readonly pricingService = inject(PricingService);
  private readonly authService = inject(AuthService);

  private readonly roomId = Number(this.route.snapshot.paramMap.get('id'));
  readonly checkIn = this.route.snapshot.queryParamMap.get('checkIn') ?? '';
  readonly checkOut = this.route.snapshot.queryParamMap.get('checkOut') ?? '';
  readonly requestedAdults = Math.max(1, Number(this.route.snapshot.queryParamMap.get('adulti') ?? 1) || 1);
  readonly requestedChildren = Math.max(0, Number(this.route.snapshot.queryParamMap.get('bambini') ?? 0) || 0);
  readonly requestedGuestCount = Math.min(8, this.requestedAdults + this.requestedChildren);
  readonly nights = this.pricingService.nightsBetween(this.checkIn, this.checkOut);
  readonly isCustomer = this.authService.isCustomer;

  readonly room = signal<Room | null>(null);
  readonly isLoadingRoom = signal(true);
  readonly isSubmitting = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly missingDates = !this.checkIn || !this.checkOut;

  readonly bookingForm = this.fb.group({
    trattamento: ['COLAZIONE', Validators.required],
    spaAbbinata: [false]
  });

  readonly anonymousForm = this.fb.nonNullable.group({
    nome: ['', [Validators.required, Validators.minLength(2)]],
    cognome: ['', [Validators.required, Validators.minLength(2)]],
    cellulare: ['', [Validators.required, Validators.pattern(/^[0-9+ ()-]{6,20}$/)]],
    email: ['', [Validators.required, Validators.email]]
  });

  private readonly spaAbbinata = toSignal(this.bookingForm.controls.spaAbbinata.valueChanges, {
    initialValue: false
  });

  readonly totalAmount = computed(() =>
    this.pricingService.calculateStayAmount(
      this.room()?.prezzoPerNotte ?? 0,
      this.nights,
      this.spaAbbinata() ?? false
    )
  );

  /** BLOCCO DIDATTICO — Carica la camera richiesta dall’URL e prepara i dati della prenotazione. */
  ngOnInit(): void {
    this.roomService.getRoomById(this.roomId).subscribe({
      next: (room) => {
        this.room.set(room);
        this.isLoadingRoom.set(false);
      },
      error: () => {
        this.errorMessage.set('Camera non trovata o backend non raggiungibile.');
        this.isLoadingRoom.set(false);
      }
    });
  }

  /** BLOCCO DIDATTICO — Centralizza la condizione visiva di errore dei controlli del form. */
  isInvalid(control: AbstractControl | null): boolean {
    return !!control && control.invalid && (control.touched || control.dirty);
  }

  /** BLOCCO DIDATTICO — Valida il form, costruisce il payload corretto e crea la prenotazione. */
  onSubmit(): void {
    const isCustomer = this.isCustomer();

    if (this.missingDates) {
      this.errorMessage.set('Date mancanti: torna alla ricerca camere e riprova.');
      return;
    }

    if (this.bookingForm.invalid || (!isCustomer && this.anonymousForm.invalid)) {
      this.bookingForm.markAllAsTouched();
      if (!isCustomer) {
        this.anonymousForm.markAllAsTouched();
      }
      this.errorMessage.set('Controlla i campi evidenziati in rosso.');
      return;
    }

    this.isSubmitting.set(true);
    this.errorMessage.set(null);
    this.clearServerErrors();

    const request$ = isCustomer
      ? this.bookingService.createBooking({
          roomId: this.roomId,
          checkIn: this.checkIn,
          checkOut: this.checkOut,
          numeroOspiti: this.requestedGuestCount
        })
      : this.bookingService.createGuestBooking({
          roomId: this.roomId,
          checkIn: this.checkIn,
          checkOut: this.checkOut,
          numeroOspiti: this.requestedGuestCount,
          ospite: this.anonymousForm.getRawValue()
        });

    request$.subscribe({
      next: (booking) => {
        this.isSubmitting.set(false);
        this.router.navigate(['/prenotazioni', booking.id, 'pagamento'], {
          queryParams: { totalAmount: this.totalAmount() }
        });
      },
      error: (error: HttpErrorResponse) => {
        this.isSubmitting.set(false);

        if (error.status === 409) {
          this.errorMessage.set('Questa camera è già prenotata nelle date selezionate.');
          return;
        }

        if (error.status === 401 || error.status === 403) {
          this.errorMessage.set('Sessione non valida. Esci e accedi di nuovo, oppure continua come ospite.');
          return;
        }

        if (error.status === 400) {
          if (!isCustomer) {
            this.anonymousForm.markAllAsTouched();
            this.applyServerFieldErrors(error);
          }
          this.errorMessage.set('Controlla i campi evidenziati in rosso e riprova.');
          return;
        }

        this.errorMessage.set('Non è stato possibile salvare la prenotazione nel database.');
      }
    });
  }

  private applyServerFieldErrors(error: HttpErrorResponse): void {
    const message = String(error.error?.message ?? error.error?.detail ?? error.message ?? '').toLowerCase();
    const fields: Array<keyof typeof this.anonymousForm.controls> = ['nome', 'cognome', 'cellulare', 'email'];

    let matched = false;
    for (const field of fields) {
      if (message.includes(field)) {
        const control = this.anonymousForm.controls[field];
        control.setErrors({ ...(control.errors ?? {}), server: true });
        matched = true;
      }
    }

    // Se il backend non indica il campo preciso, evidenziamo solo quelli già invalidi.
    // Se tutti sono formalmente validi, segnaliamo l'intero blocco senza inventare un campo.
    if (!matched) {
      this.anonymousForm.updateValueAndValidity({ emitEvent: false });
    }
  }

  private clearServerErrors(): void {
    for (const control of Object.values(this.anonymousForm.controls)) {
      if (!control.errors?.['server']) {
        continue;
      }
      const { server: _server, ...remainingErrors } = control.errors;
      control.setErrors(Object.keys(remainingErrors).length ? remainingErrors : null);
    }
  }
}
