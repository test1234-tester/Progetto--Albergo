export interface Guest {
  nome: string;
  cognome: string;
}

export interface BookingRequest {
  roomId: number;
  checkIn: string;
  checkOut: string;
  guests: Guest[];
}

export interface GuestBookingRequest extends BookingRequest {
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
  guests: Guest[];
  stato: BookingStatus;
}
