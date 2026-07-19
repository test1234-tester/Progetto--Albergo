// Rispecchia l'enum lato backend SOLO_ALBERGO / SOLO_SPA / ALBERGO_E_SPA
export type SpaMode = 'SOLO_SPA' | 'ALBERGO_E_SPA';

// Catalogo trattamenti prenotabili in modalità standalone (prezzo libero, non fisso a +200€)
export interface SpaTreatment {
  id: string;
  nome: string;
  prezzo: number;
}

// Payload per "prenota solo Spa"
export interface StandaloneSpaRequest {
  date: string;
  treatmentId: string;
}

// Payload per "aggiungi Spa a soggiorno esistente" (+200€ fissi)
export interface AddSpaToStayRequest {
  bookingId: number;
}

export interface SpaBooking {
  id: number;
  mode: SpaMode;
  bookingId?: number;
  date?: string;
  amount: number;
  stato: string;
}
