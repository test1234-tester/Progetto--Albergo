/**
 * GUIDA DIDATTICA DEL FILE
 * ---------------------------------------------------------------------------
 * Servizio HTTP dell’Area personale cliente.
 * Leggi prima gli import, poi @Component/@Injectable e infine campi e metodi:
 * questa è la stessa sequenza con cui Angular compone il comportamento del file.
 */
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import {
  UserDashboardBooking,
  UserDashboardData,
  UserDashboardProfile,
  UserProfileUpdate
} from '../models/user-dashboard.model';

@Injectable({ providedIn: 'root' })
export class UserDashboardService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/dashboard/user`;

  getDashboard(): Observable<UserDashboardData> {
    return this.http.get<UserDashboardData>(this.baseUrl);
  }

  updateProfile(payload: UserProfileUpdate): Observable<UserDashboardProfile> {
    return this.http.patch<UserDashboardProfile>(`${this.baseUrl}/profile`, payload);
  }

  updateBookingName(bookingId: number, nominativo: string): Observable<UserDashboardBooking> {
    return this.http.patch<UserDashboardBooking>(
      `${this.baseUrl}/bookings/${bookingId}/nominativo`,
      { nominativo }
    );
  }

  deleteBooking(bookingId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/bookings/${bookingId}`);
  }
}
