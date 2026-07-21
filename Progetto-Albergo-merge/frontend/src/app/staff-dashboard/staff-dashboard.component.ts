import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';

import { StaffBooking, StaffDashboard, StaffRoom } from '../core/models/staff-dashboard.model';
import { StaffDashboardService } from '../core/services/staff-dashboard.service';

type StaffSection = 'overview' | 'bookings' | 'rooms' | 'users';

@Component({
  selector: 'app-staff-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './staff-dashboard.component.html',
  styleUrl: './staff-dashboard.component.scss'
})
export class StaffDashboardComponent implements OnInit {
  private readonly dashboardService = inject(StaffDashboardService);

  readonly dashboard = signal<StaffDashboard | null>(null);
  readonly activeSection = signal<StaffSection>('overview');
  readonly isLoading = signal(true);
  readonly pendingItemId = signal<number | null>(null);
  readonly errorMessage = signal<string | null>(null);
  readonly successMessage = signal<string | null>(null);

  readonly pendingBookings = computed(
    () => this.dashboard()?.bookings.filter((item) => !item.confermata).length ?? 0
  );

  readonly occupancyPercentage = computed(() => {
    const stats = this.dashboard()?.stats;
    if (!stats || stats.camereTotali === 0) return 0;
    return Math.round((stats.camereOccupate / stats.camereTotali) * 100);
  });

  ngOnInit(): void {
    this.loadDashboard();
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
    this.clearMessages();
  }

  toggleBooking(booking: StaffBooking): void {
    this.pendingItemId.set(booking.id);
    this.clearMessages();
    this.dashboardService.updateBookingStatus(booking.id, !booking.confermata).subscribe({
      next: (updated) => {
        this.dashboard.update((current) =>
          current
            ? {
                ...current,
                bookings: current.bookings.map((item) => (item.id === updated.id ? updated : item))
              }
            : current
        );
        this.pendingItemId.set(null);
        this.successMessage.set(
          updated.confermata ? 'Prenotazione confermata.' : 'Prenotazione riportata in attesa.'
        );
      },
      error: () => {
        this.pendingItemId.set(null);
        this.errorMessage.set('Non è stato possibile aggiornare la prenotazione.');
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
          const rooms = current.rooms.map((item) => (item.id === updated.id ? updated : item));
          return {
            ...current,
            rooms,
            stats: {
              ...current.stats,
              camereOccupate: rooms.filter((item) => item.occupata).length
            }
          };
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

  private clearMessages(): void {
    this.errorMessage.set(null);
    this.successMessage.set(null);
  }
}
