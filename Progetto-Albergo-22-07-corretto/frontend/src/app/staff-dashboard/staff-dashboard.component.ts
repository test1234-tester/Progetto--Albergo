import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { AbstractControl, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';

import { StaffBooking, StaffDashboard, StaffRoom } from '../core/models/staff-dashboard.model';
import { StaffDashboardService } from '../core/services/staff-dashboard.service';

type StaffSection = 'overview' | 'bookings' | 'rooms' | 'users';

@Component({
  selector: 'app-staff-dashboard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatButtonModule],
  templateUrl: './staff-dashboard.component.html',
  styleUrl: './staff-dashboard.component.scss'
})
export class StaffDashboardComponent implements OnInit {
  private readonly dashboardService = inject(StaffDashboardService);
  private readonly fb = inject(FormBuilder);

  readonly dashboard = signal<StaffDashboard | null>(null);
  readonly activeSection = signal<StaffSection>('overview');
  readonly isLoading = signal(true);
  readonly pendingItemId = signal<number | null>(null);
  readonly deletingBookingId = signal<number | null>(null);
  readonly editingBookingId = signal<number | null>(null);
  readonly creatingBooking = signal(false);
  readonly savingBooking = signal(false);
  readonly showPhysicalBookingForm = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly successMessage = signal<string | null>(null);

  readonly physicalBookingForm = this.fb.nonNullable.group({
    nome: ['', [Validators.required, Validators.minLength(2)]],
    cognome: ['', [Validators.required, Validators.minLength(2)]],
    cellulare: ['', [Validators.required, Validators.pattern(/^[0-9+ ()-]{6,20}$/)]],
    email: ['', Validators.email],
    roomId: [0, [Validators.required, Validators.min(1)]],
    checkIn: ['', Validators.required],
    checkOut: ['', Validators.required],
    numeroOspiti: [1, [Validators.required, Validators.min(1), Validators.max(8)]]
  });

  readonly bookingEditForm = this.fb.nonNullable.group({
    nominativo: ['', [Validators.required, Validators.minLength(2)]],
    roomId: [0, [Validators.required, Validators.min(1)]],
    checkIn: ['', Validators.required],
    checkOut: ['', Validators.required],
    numeroOspiti: [1, [Validators.required, Validators.min(1), Validators.max(8)]],
    confermata: [false]
  });

  readonly pendingBookings = computed(
    () => this.dashboard()?.bookings.filter((item) => !item.confermata).length ?? 0
  );

  readonly occupancyPercentage = computed(() => {
    const stats = this.dashboard()?.stats;
    if (!stats || stats.camereTotali === 0) return 0;
    return Math.round((stats.camereOccupate / stats.camereTotali) * 100);
  });

  ngOnInit(): void {
    this.resetPhysicalBookingForm();
    this.loadDashboard();
  }

  isInvalid(control: AbstractControl | null): boolean {
    return !!control && control.invalid && (control.touched || control.dirty);
  }

  loadDashboard(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);
    this.dashboardService.getDashboard().subscribe({
      next: (dashboard) => {
        this.dashboard.set(dashboard);
        this.isLoading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.errorMessage.set(
          error.status === 403
            ? 'Accesso negato: effettua il login nell’area Staff.'
            : 'Impossibile caricare il gestionale staff.'
        );
        this.isLoading.set(false);
      }
    });
  }

  setSection(section: StaffSection): void {
    this.activeSection.set(section);
    this.cancelBookingEdit();
    this.clearMessages();
  }

  togglePhysicalBookingForm(): void {
    this.showPhysicalBookingForm.update((value) => !value);
    this.clearMessages();
  }

  createPhysicalBooking(): void {
    if (this.physicalBookingForm.invalid) {
      this.physicalBookingForm.markAllAsTouched();
      this.errorMessage.set('Compila correttamente i campi evidenziati in rosso.');
      return;
    }

    const value = this.physicalBookingForm.getRawValue();
    if (new Date(value.checkOut) <= new Date(value.checkIn)) {
      this.physicalBookingForm.controls.checkOut.setErrors({ dateOrder: true });
      this.physicalBookingForm.controls.checkOut.markAsTouched();
      this.errorMessage.set('Il check-out deve essere successivo al check-in.');
      return;
    }

    this.creatingBooking.set(true);
    this.clearMessages();
    this.dashboardService.createPhysicalBooking({
      roomId: value.roomId,
      checkIn: value.checkIn,
      checkOut: value.checkOut,
      numeroOspiti: value.numeroOspiti,
      ospite: {
        nome: value.nome,
        cognome: value.cognome,
        cellulare: value.cellulare,
        email: value.email
      }
    }).subscribe({
      next: (booking) => {
        this.dashboard.update((current) => current ? {
          ...current,
          bookings: [...current.bookings, booking].sort((a, b) => String(a.dataArrivo ?? '').localeCompare(String(b.dataArrivo ?? ''))),
          stats: { ...current.stats, prenotazioniTotali: current.stats.prenotazioniTotali + 1 }
        } : current);
        this.creatingBooking.set(false);
        this.showPhysicalBookingForm.set(false);
        this.resetPhysicalBookingForm();
        this.successMessage.set('Prenotazione in struttura registrata correttamente.');
      },
      error: (error: HttpErrorResponse) => {
        this.creatingBooking.set(false);
        this.applyServerErrors(error, this.physicalBookingForm);
        if (error.status === 409) this.errorMessage.set('La camera è già prenotata nelle date selezionate.');
        else if (error.status === 400) this.errorMessage.set('Controlla i campi evidenziati in rosso.');
        else this.errorMessage.set('Non è stato possibile creare la prenotazione in struttura.');
      }
    });
  }

  startBookingEdit(booking: StaffBooking): void {
    this.editingBookingId.set(booking.id);
    this.bookingEditForm.reset({
      nominativo: booking.cliente || booking.nominativo || '',
      roomId: booking.numeroCamera ?? 0,
      checkIn: booking.dataArrivo ?? '',
      checkOut: booking.dataPartenza ?? '',
      numeroOspiti: booking.numeroOspiti || 1,
      confermata: booking.confermata
    });
    this.clearMessages();
  }

  cancelBookingEdit(): void {
    this.editingBookingId.set(null);
    this.bookingEditForm.reset({
      nominativo: '', roomId: 0, checkIn: '', checkOut: '', numeroOspiti: 1, confermata: false
    });
  }

  saveBookingEdit(booking: StaffBooking): void {
    if (this.bookingEditForm.invalid) {
      this.bookingEditForm.markAllAsTouched();
      this.errorMessage.set('Controlla i campi evidenziati in rosso.');
      return;
    }
    const value = this.bookingEditForm.getRawValue();
    if (new Date(value.checkOut) <= new Date(value.checkIn)) {
      this.bookingEditForm.controls.checkOut.setErrors({ dateOrder: true });
      this.bookingEditForm.controls.checkOut.markAsTouched();
      this.errorMessage.set('Il check-out deve essere successivo al check-in.');
      return;
    }

    this.savingBooking.set(true);
    this.clearMessages();
    this.dashboardService.updateBooking(booking.id, value).subscribe({
      next: (updated) => {
        this.dashboard.update((current) => current ? {
          ...current,
          bookings: current.bookings.map((item) => item.id === updated.id ? updated : item)
        } : current);
        this.savingBooking.set(false);
        this.editingBookingId.set(null);
        this.successMessage.set('Prenotazione modificata nel database.');
      },
      error: (error: HttpErrorResponse) => {
        this.savingBooking.set(false);
        this.applyServerErrors(error, this.bookingEditForm);
        if (error.status === 409) this.errorMessage.set('La camera è già occupata nelle date selezionate.');
        else if (error.status === 400) this.errorMessage.set('Controlla i campi evidenziati in rosso.');
        else this.errorMessage.set('Non è stato possibile modificare la prenotazione.');
      }
    });
  }

  deleteBooking(booking: StaffBooking): void {
    if (!window.confirm(`Cancellare definitivamente la prenotazione #${booking.id}?`)) return;
    this.deletingBookingId.set(booking.id);
    this.clearMessages();
    this.dashboardService.deleteBooking(booking.id).subscribe({
      next: () => {
        this.dashboard.update((current) => current ? {
          ...current,
          bookings: current.bookings.filter((item) => item.id !== booking.id),
          stats: { ...current.stats, prenotazioniTotali: Math.max(0, current.stats.prenotazioniTotali - 1) }
        } : current);
        this.deletingBookingId.set(null);
        this.editingBookingId.set(null);
        this.successMessage.set('Prenotazione cancellata definitivamente dal database.');
      },
      error: () => {
        this.deletingBookingId.set(null);
        this.errorMessage.set('Non è stato possibile cancellare la prenotazione.');
      }
    });
  }

  toggleRoom(room: StaffRoom): void {
    const occupata = !room.occupata;
    const occupanti = occupata ? Math.max(room.occupanti, 1) : 0;
    this.pendingItemId.set(room.id);
    this.clearMessages();
    this.dashboardService.updateRoomStatus(room.id, occupata, occupanti).subscribe({
      next: (updated) => {
        this.dashboard.update((current) => {
          if (!current) return current;
          const rooms = current.rooms.map((item) => item.id === updated.id ? updated : item);
          return { ...current, rooms, stats: { ...current.stats, camereOccupate: rooms.filter((item) => item.occupata).length } };
        });
        this.pendingItemId.set(null);
        this.successMessage.set(updated.occupata ? 'Camera segnata come occupata.' : 'Camera liberata.');
      },
      error: () => {
        this.pendingItemId.set(null);
        this.errorMessage.set('Non è stato possibile aggiornare la camera.');
      }
    });
  }

  originLabel(origin: StaffBooking['origine']): string {
    switch (origin) {
      case 'STRUTTURA': return 'Struttura';
      case 'ONLINE_OSPITE': return 'Online ospite';
      case 'ONLINE_UTENTE': return 'Online account';
      default: return 'Non specificata';
    }
  }

  private applyServerErrors(error: HttpErrorResponse, form: AbstractControl): void {
    if (error.status !== 400) return;
    const message = String(error.error?.message ?? error.error?.detail ?? error.message ?? '').toLowerCase();
    const aliases: Record<string, string[]> = {
      nome: ['nome'], cognome: ['cognome'], cellulare: ['cellulare'], email: ['email'],
      roomId: ['camera'], checkIn: ['check-in', 'data'], checkOut: ['check-out', 'data'],
      numeroOspiti: ['ospiti', 'persone'], nominativo: ['nominativo']
    };
    for (const [field, tokens] of Object.entries(aliases)) {
      const control = form.get(field);
      if (control && tokens.some((token) => message.includes(token))) {
        control.setErrors({ ...(control.errors ?? {}), server: true });
        control.markAsTouched();
      }
    }
  }

  private resetPhysicalBookingForm(): void {
    const today = new Date().toISOString().split('T')[0];
    const tomorrow = new Date(Date.now() + 86400000).toISOString().split('T')[0];
    this.physicalBookingForm.reset({
      nome: '', cognome: '', cellulare: '', email: '', roomId: 0,
      checkIn: today, checkOut: tomorrow, numeroOspiti: 1
    });
  }

  private clearMessages(): void {
    this.errorMessage.set(null);
    this.successMessage.set(null);
  }
}
