import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { AuthService } from '../services/auth.service';

export const customerGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.isAuthenticated()) {
    return router.createUrlTree(['/login']);
  }
  if (auth.isCustomer()) {
    return true;
  }
  if (auth.isStaff()) {
    return router.createUrlTree(['/staff']);
  }

  // Stato anomalo: token/sessione senza un ruolo riconosciuto.
  auth.logout();
  return false;
};

export const staffGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.isAuthenticated()) {
    return router.createUrlTree(['/login']);
  }
  if (auth.isStaff()) {
    return true;
  }
  if (auth.isCustomer()) {
    return router.createUrlTree(['/area-personale']);
  }

  auth.logout();
  return false;
};
