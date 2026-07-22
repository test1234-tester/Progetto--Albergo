/**
 * GUIDA DIDATTICA DEL FILE
 * ---------------------------------------------------------------------------
 * Servizio HTTP dedicato al pagamento/caparra della prenotazione.
 * Leggi prima gli import, poi @Component/@Injectable e infine campi e metodi:
 * questa è la stessa sequenza con cui Angular compone il comportamento del file.
 */
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { PaymentRequest, PaymentResponse } from '../models/payment.model';

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/pagamenti`;

  // Solo carta, caparra 10% calcolata autoritativamente dal backend (qui solo invio + lettura esito)
  pay(payload: PaymentRequest): Observable<PaymentResponse> {
    return this.http.post<PaymentResponse>(this.baseUrl, payload);
  }
}
