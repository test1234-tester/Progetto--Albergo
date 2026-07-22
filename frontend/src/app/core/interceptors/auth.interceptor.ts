/**
 * GUIDA DIDATTICA DEL FILE
 * ---------------------------------------------------------------------------
 * Interceptor HTTP: aggiunge automaticamente il JWT alle richieste protette.
 * Leggi prima gli import, poi @Component/@Injectable e infine campi e metodi:
 * questa è la stessa sequenza con cui Angular compone il comportamento del file.
 */
//Questo file intercetta ogni singola richiesta HTTP in uscita verso il backend e, se presente, le incolla in automatico il Token JWT in testa


// Importa il tipo per gli interceptor funzionali basati sulle ultime specifiche Angular
import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';

// Importa il servizio AuthService per recuperare il token salvato
import { AuthService } from '../services/auth.service';

// Definisce l'interceptor funzionale che riceve la richiesta (req) e il gestore successivo (next)
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Inietta l'AuthService per accedere ai metodi del local storage
  const authService = inject(AuthService);
  // Recupera il token JWT salvato nel browser (restituisce la stringa o null)
  const token = authService.getToken();

  // Se il token non esiste (ad esempio: l'utente non è loggato o sta visitando pagine pubbliche)
  if (!token) {
    // Lascia andare la richiesta originale senza modificarla
    return next(req);
  }

  // Se il token esiste, clona la richiesta originale (le richieste HTTP sono immutabili e non modificabili direttamente)
 // e aggiunge l'header 'Authorization' nel formato standard "Bearer"richiesto da Spring Boot
  return next(
    req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    })
  );
};
