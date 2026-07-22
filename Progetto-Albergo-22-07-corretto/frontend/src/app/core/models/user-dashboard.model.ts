export interface UserDashboardProfile {
  idUtente: number;
  nome: string;
  cognome: string;
  username: string;
  cellulare: string;
  email: string;
}

export interface UserDashboardBooking {
  id: number;
  camera: string;
  immagine: string | null;
  idCamera: number | null;
  dataPrenotazione: string | null;
  dataArrivo: string | null;
  dataPartenza: string | null;
  nominativo: string;
  confermata: boolean;
  prezzoPerNotte: number | null;
  totaleStimato: number | null;
}

export interface UserDashboardData {
  profile: UserDashboardProfile;
  bookings: UserDashboardBooking[];
}

export interface UserProfileUpdate {
  nome: string;
  cognome: string;
  username: string;
  cellulare: string;
}
