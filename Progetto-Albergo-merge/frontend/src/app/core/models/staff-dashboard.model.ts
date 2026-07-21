export interface StaffStats {
  camereTotali: number;
  camereOccupate: number;
  prenotazioniTotali: number;
  clientiRegistrati: number;
  pagamentiInPendenza: number;
}

export interface StaffBooking {
  id: number;
  idUtente: number | null;
  cliente: string;
  email: string;
  camera: string;
  numeroCamera: number | null;
  dataArrivo: string | null;
  dataPartenza: string | null;
  nominativo: string;
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
