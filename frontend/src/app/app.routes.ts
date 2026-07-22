/**
 * GUIDA DIDATTICA DEL FILE
 * ---------------------------------------------------------------------------
 * Tabella di routing: associa URL, componenti lazy-loaded, guardie e titoli pagina.
 * Leggi prima gli import, poi @Component/@Injectable e infine campi e metodi:
 * questa è la stessa sequenza con cui Angular compone il comportamento del file.
 */
import { Routes } from '@angular/router';

import { HomeComponent } from './homepage/home.component';
import { authGuard } from './core/guards/auth.guard';
import { customerGuard, staffGuard } from './core/guards/role.guard';

export const routes: Routes = [
  { path: '', component: HomeComponent, title: 'Grand Hotel Minerva' },
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
    path: 'stanze',
    loadComponent: () => import('./rooms/rooms.component').then((m) => m.RoomsComponent),
    title: 'Camere'
  },
  {
    path: 'spa',
    loadComponent: () => import('./spa/spa.component').then((m) => m.SpaComponent),
    title: 'Spa'
  },
  {
    path: 'stanze/:id/prenota',
    loadComponent: () =>
      import('./rooms/booking/booking-form.component').then((m) => m.BookingFormComponent),
    title: 'Prenota camera'
  },
  {
    path: 'prenotazioni/:id/pagamento',
    loadComponent: () => import('./payment/payment.component').then((m) => m.PaymentComponent),
    title: 'Pagamento caparra'
  },
  {
    path: 'area-personale',
    loadComponent: () =>
      import('./user-dashboard/user-dashboard.component').then((m) => m.UserDashboardComponent),
    canActivate: [authGuard, customerGuard],
    title: 'Area personale'
  },
  {
    path: 'staff',
    loadComponent: () =>
      import('./staff-dashboard/staff-dashboard.component').then((m) => m.StaffDashboardComponent),
    canActivate: [authGuard, staffGuard],
    title: 'Gestionale staff'
  },
  {
    path: 'area-personale-demo',
    loadComponent: () =>
      import('./user-settings/user-settings.component').then((m) => m.UserSettingsComponent),
    canActivate: [authGuard, customerGuard],
    title: 'Prototipo area personale'
  },
  { path: 'gestionale-utente', redirectTo: 'area-personale', pathMatch: 'full' },
  { path: '**', redirectTo: '' }
];
