/**
 * GUIDA DIDATTICA DEL FILE
 * ---------------------------------------------------------------------------
 * Servizio HTTP per recuperare camere e disponibilità.
 * Leggi prima gli import, poi @Component/@Injectable e infine campi e metodi:
 * questa è la stessa sequenza con cui Angular compone il comportamento del file.
 */
import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Room } from '../models/room.model';

@Injectable({ providedIn: 'root' })
export class RoomService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/camera`;

  // checkIn/checkOut opzionali: per ora il backend (progettoalbergo) li ignora e restituisce
  // sempre il catalogo completo — il filtro per disponibilità arriverà con l'entità Prenotazione
  // (Sviluppatore 1, Giorno 2). I parametri sono già qui pronti per quando sarà attivo.
  getRooms(checkIn?: string, checkOut?: string): Observable<Room[]> {
    let params = new HttpParams();
    if (checkIn) {
      params = params.set('checkIn', checkIn);
    }
    if (checkOut) {
      params = params.set('checkOut', checkOut);
    }
    return this.http.get<Room[]>(this.baseUrl, { params });
  }

  getRoomById(id: number): Observable<Room> {
    return this.http.get<Room>(`${this.baseUrl}/${id}`);
  }
}
