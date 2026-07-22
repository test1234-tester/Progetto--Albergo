import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';

import {
  UserDashboardBooking,
  UserDashboardData
} from '../core/models/user-dashboard.model';
import { UserDashboardService } from '../core/services/user-dashboard.service';
import { AuthService } from '../core/services/auth.service';

@Component({
  selector: 'app-user-dashboard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
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
    nome: ['', Validators.required],
    cognome: ['', Validators.required],
    username: ['', Validators.required],
    cellulare: ['']
  });

  readonly bookingNameForm = this.fb.nonNullable.group({
    nominativo: ['', Validators.required]
  });

  ngOnInit(): void {
    this.loadDashboard();
  }

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

  saveProfile(): void {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
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
      error: () => {
        this.errorMessage.set('Non è stato possibile aggiornare il profilo.');
        this.isSavingProfile.set(false);
      }
    });
  }

  startBookingEdit(booking: UserDashboardBooking): void {
    this.editingBookingId.set(booking.id);
    this.bookingNameForm.setValue({ nominativo: booking.nominativo ?? '' });
    this.clearMessages();
  }

  cancelBookingEdit(): void {
    this.editingBookingId.set(null);
    this.bookingNameForm.reset();
  }

  saveBookingName(bookingId: number): void {
    if (this.bookingNameForm.invalid) {
      this.bookingNameForm.markAllAsTouched();
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
          this.successMessage.set('Nominativo aggiornato nel database.');
          this.isSavingBooking.set(false);
        },
        error: () => {
          this.errorMessage.set('Non è stato possibile modificare questa prenotazione.');
          this.isSavingBooking.set(false);
        }
      });
  }

  private clearMessages(): void {
    this.errorMessage.set(null);
    this.successMessage.set(null);
  }
}
