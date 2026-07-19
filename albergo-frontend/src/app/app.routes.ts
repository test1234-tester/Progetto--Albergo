import { Routes } from '@angular/router';

import { HomeComponent } from './homepage/home.component';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', component: HomeComponent, title: 'Albergo Online' },
  {
    path: 'login',
    loadComponent: () => import('./auth/login/login.component').then((m) => m.LoginComponent),
    title: 'Accedi'
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./auth/register/register.component').then((m) => m.RegisterComponent),
    title: 'Registrati'
  },
  {
    // Consultazione pubblica: non richiede login
    path: 'stanze',
    loadComponent: () => import('./rooms/rooms.component').then((m) => m.RoomsComponent),
    title: 'Stanze'
  },
  {
    // Compilare i dati di prenotazione richiede invece un utente autenticato
    path: 'stanze/:id/prenota',
    loadComponent: () =>
      import('./rooms/booking/booking-form.component').then((m) => m.BookingFormComponent),
    canActivate: [authGuard],
    title: 'Prenota stanza'
  },
  {
    path: 'spa',
    loadComponent: () => import('./spa/spa.component').then((m) => m.SpaComponent),
    title: 'Spa'
  },
  { path: '**', redirectTo: '' }
];
