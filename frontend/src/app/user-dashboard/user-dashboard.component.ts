/**
 * GUIDA DIDATTICA DEL FILE
 * ---------------------------------------------------------------------------
 * Area personale: profilo, elenco prenotazioni, modifica e cancellazione.
 * Leggi prima gli import, poi @Component/@Injectable e infine campi e metodi:
 * questa è la stessa sequenza con cui Angular compone il comportamento del file.
 */
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { AbstractControl, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';

import {
  UserDashboardBooking,
  UserDashboardData
} from '../core/models/user-dashboard.model';
import { AuthService } from '../core/services/auth.service';
import { UserDashboardService } from '../core/services/user-dashboard.service';

@Component({
  selector: 'app-user-dashboard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, MatButtonModule],
  templateUrl: './user-dashboard.component.html',
  styleUrl: './user-dashboard.component.scss'
})
export class UserDashboardComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly dashboardService = inject(UserDashboardService);
  private readonly authService = inject(AuthService);

  readonly dashboard = signal<UserDashboardData | null>(null);
  readonly isLoading = signal(true);
  readonly isSavingProfile = signal(false);
  readonly isSavingBooking = signal(false);
  readonly deletingBookingId = signal<number | null>(null);
  readonly editingBookingId = signal<number | null>(null);
  readonly errorMessage = signal<string | null>(null);
  readonly successMessage = signal<string | null>(null);

  readonly futureBookings = computed(() => {
    const today = new Date().toISOString().slice(0, 10);
    return this.dashboard()?.bookings.filter(
      (booking) => booking.dataPartenza != null && booking.dataPartenza >= today
    ).length ?? 0;
  });

  readonly confirmedBookings = computed(
    () => this.dashboard()?.bookings.filter((booking) => booking.confermata).length ?? 0
  );

  readonly profileForm = this.fb.nonNullable.group({
    nome: ['', [Validators.required, Validators.minLength(2)]],
    cognome: ['', [Validators.required, Validators.minLength(2)]],
    username: ['', [Validators.required, Validators.minLength(3)]],
    cellulare: ['', Validators.pattern(/^[0-9+ ()-]{6,20}$/)]
  });

  readonly bookingNameForm = this.fb.nonNullable.group({
    nominativo: ['', [Validators.required, Validators.minLength(2)]]
  });

  /** BLOCCO DIDATTICO — Avvia il caricamento dell’Area personale. */
  ngOnInit(): void {
    this.loadDashboard();
  }

  /** BLOCCO DIDATTICO — Utility per evidenziare i campi invalidi. */
  isInvalid(control: AbstractControl | null): boolean {
    return !!control && control.invalid && (control.touched || control.dirty);
  }

  /** BLOCCO DIDATTICO — Richiede al backend profilo e prenotazioni dell’utente autenticato. */
  loadDashboard(): void {
    this.isLoading.set(true);
    this.clearMessages();

    this.dashboardService.getDashboard().subscribe({
      next: (data) => {
        this.dashboard.set(data);
        this.profileForm.setValue({
          nome: data.profile.nome ?? '',
          cognome: data.profile.cognome ?? '',
          username: data.profile.username ?? '',
          cellulare: data.profile.cellulare ?? ''
        });
        this.isLoading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        if (error.status === 401) {
          this.errorMessage.set('Sessione non valida o scaduta. Esci e rifai il login.');
        } else if (error.status === 404) {
          this.errorMessage.set('Endpoint del gestionale non trovato: verifica di aver avviato la cartella backend corretta.');
        } else if (error.status === 0) {
          this.errorMessage.set('Spring Boot non è raggiungibile sulla porta 8080.');
        } else {
          this.errorMessage.set(`Errore ${error.status} durante il caricamento del gestionale.`);
        }
        this.isLoading.set(false);
      }
    });
  }

  /** BLOCCO DIDATTICO — Valida e salva le modifiche anagrafiche del profilo. */
  saveProfile(): void {
    this.clearProfileServerErrors();
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      this.errorMessage.set('Controlla i campi evidenziati in rosso.');
      return;
    }

    this.isSavingProfile.set(true);
    this.clearMessages();

    this.dashboardService.updateProfile(this.profileForm.getRawValue()).subscribe({
      next: (profile) => {
        this.dashboard.update((current) => (current ? { ...current, profile } : current));
        this.authService.updateCurrentUser(profile);
        this.successMessage.set('Profilo aggiornato nel database.');
        this.isSavingProfile.set(false);
      },
      error: (error: HttpErrorResponse) => {
        if (error.status === 400) {
          this.applyProfileServerErrors(error);
          this.errorMessage.set('Controlla i campi evidenziati in rosso.');
        } else {
          this.errorMessage.set('Non è stato possibile aggiornare il profilo.');
        }
        this.isSavingProfile.set(false);
      }
    });
  }

  /** BLOCCO DIDATTICO — Apre la modalità modifica per una prenotazione. */
  startBookingEdit(booking: UserDashboardBooking): void {
    this.editingBookingId.set(booking.id);
    this.bookingNameForm.setValue({ nominativo: booking.nominativo ?? '' });
    this.clearMessages();
  }

  /** BLOCCO DIDATTICO — Chiude la modifica senza salvare. */
  cancelBookingEdit(): void {
    this.editingBookingId.set(null);
    this.bookingNameForm.reset();
  }

  /** BLOCCO DIDATTICO — Invia al backend le modifiche consentite sulla prenotazione. */
  saveBookingName(bookingId: number): void {
    if (this.bookingNameForm.invalid) {
      this.bookingNameForm.markAllAsTouched();
      this.errorMessage.set('Controlla il nominativo evidenziato in rosso.');
      return;
    }

    this.isSavingBooking.set(true);
    this.clearMessages();

    this.dashboardService
      .updateBookingName(bookingId, this.bookingNameForm.controls.nominativo.value)
      .subscribe({
        next: (updatedBooking) => {
          this.dashboard.update((current) =>
            current
              ? {
                  ...current,
                  bookings: current.bookings.map((booking) =>
                    booking.id === updatedBooking.id ? updatedBooking : booking
                  )
                }
              : current
          );
          this.editingBookingId.set(null);
          this.successMessage.set('Prenotazione aggiornata nel database.');
          this.isSavingBooking.set(false);
        },
        error: (error: HttpErrorResponse) => {
          if (error.status === 400) {
            this.bookingNameForm.controls.nominativo.setErrors({
              ...(this.bookingNameForm.controls.nominativo.errors ?? {}),
              server: true
            });
            this.errorMessage.set('Controlla il nominativo evidenziato in rosso.');
          } else {
            this.errorMessage.set('Non è stato possibile modificare questa prenotazione.');
          }
          this.isSavingBooking.set(false);
        }
      });
  }

  /** BLOCCO DIDATTICO — Chiede conferma e cancella la prenotazione dal database tramite API. */
  deleteBooking(booking: UserDashboardBooking): void {
    const confirmed = window.confirm(
      `Vuoi cancellare definitivamente la prenotazione #${booking.id} per ${booking.camera}?`
    );
    if (!confirmed) return;

    this.deletingBookingId.set(booking.id);
    this.clearMessages();
    this.dashboardService.deleteBooking(booking.id).subscribe({
      next: () => {
        this.dashboard.update((current) =>
          current
            ? { ...current, bookings: current.bookings.filter((item) => item.id !== booking.id) }
            : current
        );
        this.editingBookingId.set(null);
        this.deletingBookingId.set(null);
        this.successMessage.set('Prenotazione cancellata definitivamente dal database.');
      },
      error: (error: HttpErrorResponse) => {
        this.deletingBookingId.set(null);
        this.errorMessage.set(
          error.status === 409
            ? 'La prenotazione non può essere cancellata perché contiene dati collegati.'
            : 'Non è stato possibile cancellare la prenotazione.'
        );
      }
    });
  }

  private applyProfileServerErrors(error: HttpErrorResponse): void {
    const message = String(error.error?.message ?? error.error?.detail ?? error.message ?? '').toLowerCase();
    const fields: Array<keyof typeof this.profileForm.controls> = ['nome', 'cognome', 'username', 'cellulare'];
    for (const field of fields) {
      if (message.includes(field)) {
        const control = this.profileForm.controls[field];
        control.setErrors({ ...(control.errors ?? {}), server: true });
      }
    }
  }

  private clearProfileServerErrors(): void {
    for (const control of Object.values(this.profileForm.controls)) {
      if (!control.errors?.['server']) continue;
      const { server: _server, ...remaining } = control.errors;
      control.setErrors(Object.keys(remaining).length ? remaining : null);
    }
  }

  private clearMessages(): void {
    this.errorMessage.set(null);
    this.successMessage.set(null);
  }
}
