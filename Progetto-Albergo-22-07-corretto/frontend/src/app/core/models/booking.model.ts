export interface Guest {
  nome: string;
  cognome: string;
}

// Payload inviato a POST /prenotazioni
export interface BookingRequest {
  roomId: number;
  checkIn: string; // formato ISO yyyy-MM-dd
  checkOut: string;
  guests: Guest[];
}

// Stato gestito lato backend (scheduler annullamento 48h, Giorno 4)
export type BookingStatus = 'IN_ATTESA' | 'CONFERMATA' | 'SCADUTA' | 'ANNULLATA';

export interface Booking {
  id: number;
  roomId: number;
  checkIn: string;
  checkOut: string;
  guests: Guest[];
  stato: BookingStatus;
}
