//Mantiene lo stato dell'utente e scrive/legge nel localStorage del browser


// Importa le utility di Angular, inclusi i moduli per la gestione dei Signals reattivi[cite: 1]
import { Injectable, computed, inject, signal } from '@angular/core';
// Importa il client HTTP per effettuare le chiamate verso le API[cite: 1]
import { HttpClient } from '@angular/common/http';
// Importa il router per gestire i cambi di pagina[cite: 1]
import { Router } from '@angular/router';
// Importa le utility di RxJS per gestire i flussi asincroni e intercettare le risposte[cite: 1]
import { Observable, tap } from 'rxjs';

// Importa l'oggetto environment che contiene l'indirizzo IP/URL di base del backend Spring Boot[cite: 1]
import { environment } from '../../../environments/environment';
// Importa le interfacce per la corretta tipizzazione di richieste e risposte[cite: 1]
import { AuthResponse, LoginRequest, RegisterRequest, User } from '../models/auth.model';

// Costanti interne per definire le chiavi di memorizzazione all'interno del LocalStorage del browser[cite: 1]
const TOKEN_KEY = 'albergo_token';
const USER_KEY = 'albergo_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  // Inietta il client HTTP per inviare dati al server[cite: 1]
  private readonly http = inject(HttpClient);
  // Inietta il router per reindirizzare dopo il logout[cite: 1]
  private readonly router = inject(Router);
  // Costruisce l'URL completo combinando l'indirizzo base del backend con l'endpoint specifico /auth[cite: 1]
  private readonly baseUrl = `${environment.apiUrl}/auth`;

  // 🔥 SIGNAL PRIVATO: Memorizza l'utente attualmente loggato. 
  // All'avvio dell'applicazione legge immediatamente il localStorage per vedere se c'era una sessione attiva[cite: 1]
  private readonly _currentUser = signal<User | null>(this.readStoredUser());
  
  // EXPOSE READONLY SIGNAL: Espone l'utente all'esterno in modalità sola lettura per sicurezza[cite: 1]
  readonly currentUser = this._currentUser.asReadonly();
  
  // SIGNAL DERIVATO (COMPUTED): Restituisce true se l'utente è loggato (diverso da null), false altrimenti[cite: 1]
  readonly isAuthenticated = computed(() => this._currentUser() !== null);

  // Esegue la chiamata HTTP POST per effettuare il login[cite: 1]
  login(payload: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.baseUrl}/login`, payload)
      // Utilizza l'operatore tap per intercettare la risposta di successo e salvare i dati in sessione[cite: 1]
      .pipe(tap((res) => this.setSession(res)));
  }

  // Esegue la chiamata HTTP POST per registrare un nuovo account[cite: 1]
  register(payload: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.baseUrl}/register`, payload)
      // Se ha successo, esegue il login automatico salvando la sessione appena creata[cite: 1]
      .pipe(tap((res) => this.setSession(res)));
  }

  // Cancella completamente la sessione corrente dell'utente[cite: 1]
  logout(): void {
    localStorage.removeItem(TOKEN_KEY); // Rimuove il token JWT dal browser[cite: 1]
    localStorage.removeItem(USER_KEY);  // Rimuove i dati dell'utente dal browser[cite: 1]
    this._currentUser.set(null);        // Aggiorna il Signal impostandolo a null (tutta l'interfaccia si adeguerà)[cite: 1]
    this.router.navigate(['/login']);   // Rispedisce l'utente alla schermata di login[cite: 1]
  }

  // Metodo rapido per recuperare la stringa pura del Token JWT (usato dall'interceptor)[cite: 1]
  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  // Salva i dati fisicamente all'interno del browser dell'utente[cite: 1]
  private setSession(res: AuthResponse): void {
    localStorage.setItem(TOKEN_KEY, res.token); // Salva la stringa del token[cite: 1]
    localStorage.setItem(USER_KEY, JSON.stringify(res.user)); // Converte l'oggetto utente in stringa JSON e lo salva[cite: 1]
    this._currentUser.set(res.user); // Aggiorna lo stato del Signal con le info dell'utente appena loggato[cite: 1]
  }

  // Legge e decodifica i dati dell'utente salvati nel browser al rinfresco della pagina[cite: 1]
  private readStoredUser(): User | null {
    const raw = localStorage.getItem(USER_KEY);
    // Se non trova nulla, significa che nessun utente era loggato[cite: 1]
    if (!raw) {
      return null;
    }
    try {
      // Converte la stringa JSON memorizzata in un oggetto TypeScript valido[cite: 1]
      return JSON.parse(raw) as User;
    } catch {
      // Se il localStorage risulta corrotto o manomesso, pulisce la chiave per sicurezza e restituisce null[cite: 1]
      localStorage.removeItem(USER_KEY);
      return null;
    }
  }
}