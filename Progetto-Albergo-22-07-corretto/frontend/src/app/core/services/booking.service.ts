import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Booking, BookingRequest, GuestBookingRequest } from '../models/booking.model';

@Injectable({ providedIn: 'root' })
export class BookingService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/prenotazioni`;

  createBooking(payload: BookingRequest): Observable<Booking> {
    return this.http.post<Booking>(this.baseUrl, payload);
  }

  createGuestBooking(payload: GuestBookingRequest): Observable<Booking> {
    return this.http.post<Booking>(`${this.baseUrl}/ospite`, payload);
  }
}
