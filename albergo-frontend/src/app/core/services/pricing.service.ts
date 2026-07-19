import { Injectable } from '@angular/core';

// Sovrapprezzo fisso quando la Spa viene abbinata al soggiorno (traccia: +200€)
export const SPA_ABBINATA_PRICE = 200;

@Injectable({ providedIn: 'root' })
export class PricingService {
  // Numero di notti tra due date ISO (yyyy-MM-dd)
  nightsBetween(checkIn: string, checkOut: string): number {
    if (!checkIn || !checkOut) {
      return 0;
    }
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
