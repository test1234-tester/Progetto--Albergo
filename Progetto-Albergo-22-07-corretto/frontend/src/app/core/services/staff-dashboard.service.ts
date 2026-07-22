import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import {
  PhysicalBookingRequest,
  StaffBooking,
  StaffBookingUpdate,
  StaffDashboard,
  StaffRoom
} from '../models/staff-dashboard.model';

@Injectable({ providedIn: 'root' })
export class StaffDashboardService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/staff`;

  getDashboard(): Observable<StaffDashboard> {
    return this.http.get<StaffDashboard>(`${this.baseUrl}/dashboard`);
  }

  createPhysicalBooking(payload: PhysicalBookingRequest): Observable<StaffBooking> {
    return this.http.post<StaffBooking>(`${this.baseUrl}/bookings`, payload);
  }

  updateBooking(bookingId: number, payload: StaffBookingUpdate): Observable<StaffBooking> {
    return this.http.patch<StaffBooking>(`${this.baseUrl}/bookings/${bookingId}`, payload);
  }

  deleteBooking(bookingId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/bookings/${bookingId}`);
  }

  updateBookingStatus(bookingId: number, confermata: boolean): Observable<StaffBooking> {
    return this.http.patch<StaffBooking>(`${this.baseUrl}/bookings/${bookingId}/status`, { confermata });
  }

  updateRoomStatus(roomId: number, occupata: boolean, occupanti: number): Observable<StaffRoom> {
    return this.http.patch<StaffRoom>(`${this.baseUrl}/rooms/${roomId}/status`, {
      occupata,
      occupanti
    });
  }
}
