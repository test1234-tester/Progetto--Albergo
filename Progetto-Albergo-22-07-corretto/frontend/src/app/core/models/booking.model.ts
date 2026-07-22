export interface BookingRequest {
  roomId: number;
  checkIn: string;
  checkOut: string;
  numeroOspiti: number;
}

export interface GuestBookingRequest {
  roomId: number;
  checkIn: string;
  checkOut: string;
  numeroOspiti: number;
  ospite: {
    nome: string;
    cognome: string;
    cellulare: string;
    email: string;
  };
}

export type BookingStatus = 'IN_ATTESA' | 'CONFERMATA' | 'SCADUTA' | 'ANNULLATA';

export interface Booking {
  id: number;
  roomId: number;
  checkIn: string;
  checkOut: string;
  numeroOspiti?: number;
  origine?: 'ONLINE_UTENTE' | 'ONLINE_OSPITE' | 'STRUTTURA';
  stato: BookingStatus;
}
