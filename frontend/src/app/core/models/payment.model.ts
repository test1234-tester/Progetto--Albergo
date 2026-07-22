/**
 * GUIDA DIDATTICA DEL FILE
 * ---------------------------------------------------------------------------
 * Tipi usati dal flusso di pagamento.
 * Leggi prima gli import, poi @Component/@Injectable e infine campi e metodi:
 * questa è la stessa sequenza con cui Angular compone il comportamento del file.
 */
export interface PaymentRequest {
  bookingId: number;
  cardHolder: string;
  cardNumber: string;
  expiry: string;
  cvv: string;
}

export interface PaymentResponse {
  id?: number;
  stato: string;
  importo?: number;
}