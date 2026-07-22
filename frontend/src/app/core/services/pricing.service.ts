/**
 * GUIDA DIDATTICA DEL FILE
 * ---------------------------------------------------------------------------
 * Servizio puro di calcolo: notti, prezzi e totali.
 * Leggi prima gli import, poi @Component/@Injectable e infine campi e metodi:
 * questa è la stessa sequenza con cui Angular compone il comportamento del file.
 */
//Questo servizio centralizza la logica finanziaria dell'albergo sul client prima di inviare i dati al pagamento


// Importa il decoratore Injectable per rendere questa classe un servizio utilizzabile ovunque
import { Injectable } from '@angular/core';

// Sovrapprezzo fisso quando la Spa viene abbinata al soggiorno (traccia: +200€)
export const SPA_ABBINATA_PRICE = 200;

// Registra il servizio a livello radice (root), rendendolo disponibile in tutta l'app
@Injectable({ providedIn: 'root' })
export class PricingService {
  // Numero di notti tra due date (yyyy-MM-dd)
  nightsBetween(checkIn: string, checkOut: string): number {
    // Se una delle due date manca, restituisce zero immediatamente
    if (!checkIn || !checkOut) {
      return 0;
    }
    // Converte le stringhe in oggetti Date nativi di JavaScript
    const start = new Date(checkIn);
    const end = new Date(checkOut);
    const diffMs = end.getTime() - start.getTime();
    return Math.max(0, Math.round(diffMs / (1000 * 60 * 60 * 24)));
  }

  // Importo totale del soggiorno: prezzo camera * notti, + 200€ se la Spa è abbinata.
  // La Spa "da sola" ha invece un prezzo libero (vedi SpaService.getTreatments) e non entra qui.
  calculateStayAmount(pricePerNight: number, nights: number, spaAbbinata: boolean): number {
    const roomAmount = pricePerNight * nights;
    const spaAmount = spaAbbinata ? SPA_ABBINATA_PRICE : 0;
    return roomAmount + spaAmount;
  }
}
