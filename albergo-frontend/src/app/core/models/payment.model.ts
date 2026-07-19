// Rispecchia l'enum lato backend (Sviluppatore 1, Giorno 3)
export type StayTreatment = 'COLAZIONE' | 'MEZZA_PENSIONE' | 'PENSIONE_COMPLETA';

export interface PaymentRequest {
  bookingId: number;
  cardNumber: string;
  cardHolder: string;
  expiry: string; // MM/YY
  cvv: string;
}

// Stato iniziale sempre "in attesa conferma": la conferma effettiva arriva dal backend
export type PaymentStatus = 'IN_ATTESA_CONFERMA' | 'CONFERMATO' | 'RIFIUTATO';

export interface PaymentResponse {
  id: number;
  bookingId: number;
  importoTotale: number;
  caparra: number;
  saldo: number;
  stato: PaymentStatus;
}
