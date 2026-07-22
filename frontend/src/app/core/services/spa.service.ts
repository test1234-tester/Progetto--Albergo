/**
 * GUIDA DIDATTICA DEL FILE
 * ---------------------------------------------------------------------------
 * Servizio HTTP relativo ai dati/prenotazioni SPA.
 * Leggi prima gli import, poi @Component/@Injectable e infine campi e metodi:
 * questa è la stessa sequenza con cui Angular compone il comportamento del file.
 */
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { SpaBooking, SpaTreatment, StandaloneSpaRequest } from '../models/spa.model';

@Injectable({ providedIn: 'root' })
export class SpaService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/spa`;

  // Catalogo trattamenti per la prenotazione Spa standalone (prezzo libero, non fisso)
  getTreatments(): Observable<SpaTreatment[]> {
    return this.http.get<SpaTreatment[]>(`${this.baseUrl}/trattamenti`);
  }

  // Prenota solo Spa, indipendente da un soggiorno
  bookStandaloneSpa(payload: StandaloneSpaRequest): Observable<SpaBooking> {
    return this.http.post<SpaBooking>(this.baseUrl, {
      mode: 'SOLO_SPA',
      ...payload
    });
  }

  // Aggiunge Spa a un soggiorno già prenotato: importo fisso +200€ (vedi PricingService)
  addSpaToStay(bookingId: number): Observable<SpaBooking> {
    return this.http.post<SpaBooking>(this.baseUrl, {
      mode: 'ALBERGO_E_SPA',
      bookingId
    });
  }
}
