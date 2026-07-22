/**
 * GUIDA DIDATTICA DEL FILE
 * ---------------------------------------------------------------------------
 * Tipi dei dati restituiti al gestionale staff.
 * Leggi prima gli import, poi @Component/@Injectable e infine campi e metodi:
 * questa è la stessa sequenza con cui Angular compone il comportamento del file.
 */
export interface StaffStats {
  camereTotali: number;
  camereOccupate: number;
  prenotazioniTotali: number;
  clientiRegistrati: number;
  pagamentiInPendenza: number;
}

export type BookingOrigin = 'ONLINE_UTENTE' | 'ONLINE_OSPITE' | 'STRUTTURA' | 'NON_SPECIFICATA';

export interface StaffBooking {
  id: number;
  idUtente: number | null;
  idOspite?: number | null;
  cliente: string;
  email: string;
  camera: string;
  numeroCamera: number | null;
  dataArrivo: string | null;
  dataPartenza: string | null;
  nominativo: string;
  numeroOspiti: number;
  origine: BookingOrigin;
  confermata: boolean;
}

export interface StaffRoom {
  id: number;
  numero: number;
  nome: string;
  prezzoPerNotte: number;
  occupata: boolean;
  occupanti: number;
}

export interface StaffUser {
  id: number;
  nominativo: string;
  username: string;
  email: string;
  cellulare: string;
}

export interface StaffDashboard {
  stats: StaffStats;
  bookings: StaffBooking[];
  rooms: StaffRoom[];
  users: StaffUser[];
}

export interface PhysicalBookingRequest {
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

export interface StaffBookingUpdate {
  roomId: number;
  checkIn: string;
  checkOut: string;
  numeroOspiti: number;
  nominativo: string;
  confermata: boolean;
}
