export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  nome: string;
  cognome: string;
  username: string;
  cellulare: string;
  email: string;
  password: string;
}

// I nomi dei campi (idUtente incluso) rispecchiano 1:1 l'entita' Utente del backend:
// Jackson serializza getIdUtente() come "idUtente", non "id"
export interface User {
  idUtente: number;
  nome: string;
  cognome: string;
  username: string;
  cellulare: string;
  email: string;
}

// Risposta attesa dagli endpoint /auth/login e /auth/register (JWT + dati utente)
export interface AuthResponse {
  token: string;
  user: User;
}
