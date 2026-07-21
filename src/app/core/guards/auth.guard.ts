// Importa la funzione per iniettare i servizi senza passare dal costruttore classico
import { inject } from '@angular/core';
// Importa il tipo specifico per le guardie funzionali e lo strumento per navigare tra le rotte
import { CanActivateFn, Router } from '@angular/router';

import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (_route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Utilizza il Signal 'isAuthenticated' del servizio per fare la verifica
  if (authService.isAuthenticated()) {
    // Se l'utente è autenticato, restituisce true e permette l'accesso alla pagina richiesta
    return true;
  }

  // Se NON è autenticato, lo rimanda alla pagina di login
  // Salva l'URL richiesto per poterci tornare dopo il login
  router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
 
  // Restituisce false per bloccare il caricamento della rotta protetta
  return false;
};
