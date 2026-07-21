export type UserRole = 'CLIENTE' | 'STAFF';
export type LoginArea = 'CLIENTE' | 'STAFF';

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

export interface User {
  idUtente: number;
  nome: string;
  cognome: string;
  username: string;
  cellulare: string;
  email: string;
  role: UserRole;
}

export interface AuthResponse {
  token: string;
  user: User;
}
