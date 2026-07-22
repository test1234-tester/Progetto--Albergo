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