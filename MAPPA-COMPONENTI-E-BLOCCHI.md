# Mappa dei componenti frontend

Questa pagina serve come indice mentre studi il progetto in Visual Studio Code.

| Componente | Cosa insegna / cosa fa |
|---|---|
| `homepage/HomeComponent` | Hero, Material Card/Button, contenuti dichiarativi, animazioni CSS, responsive. |
| `layout/NavbarComponent` | Router, autenticazione condizionale, Angular Material, ThemeService light/dark. |
| `layout/FooterComponent` | Componente presentazionale globale e RouterLink. |
| `auth/LoginComponent` | Reactive Forms, validazione, signal, login cliente/staff, password toggle. |
| `auth/RegisterComponent` | Reactive Forms, validazione incrociata password, errori server, registrazione. |
| `rooms/RoomsComponent` | Flusso completo camere: ricerca, filtri, preventivo, ospite, pagamento simulato, API. |
| `rooms/booking/BookingFormComponent` | Prenotazione di una camera specifica e differenza utente/ospite. |
| `shared/RoomCardComponent` | `@Input`, componente riutilizzabile e navigazione con query params. |
| `spa/SpaComponent` | Carosello, stato locale, form template-driven, modale, effetti hover. |
| `UserDashboardComponent` | Profilo, lettura/modifica/cancellazione delle proprie prenotazioni. |
| `StaffDashboardComponent` | Dashboard, statistiche, tabelle, creazione/modifica/cancellazione prenotazioni. |
| `PaymentComponent` | Reactive Form pagamento e simulazione del flusso di conferma. |
| `UserSettingsComponent` | Prototipo didattico con signal + Reactive Form. |

## File trasversali da leggere

### `core/services/theme.service.ts`
Mostra bene un servizio globale semplice con `signal`, `computed`, DOM e localStorage.

### `core/services/auth.service.ts`
Gestisce autenticazione, token e dati dell'utente corrente.

### `core/interceptors/auth.interceptor.ts`
Aggiunge il JWT alle richieste HTTP protette.

### `core/guards/auth.guard.ts` e `role.guard.ts`
Proteggono le route in base a login e ruolo.

### `styles.css`
È il vero design system: token light/dark, validazione, Bootstrap, Material e accessibilità.

### `material-theme.scss`
Mostra come personalizzare i system token di Angular Material.

## Percorso consigliato per studiarlo

1. `homepage/home.component.*`
2. `layout/navbar/*` + `theme.service.ts`
3. `auth/login/*`
4. `auth/register/*`
5. `shared/room-card/*`
6. `rooms/rooms.component.*`
7. `core/services/booking.service.ts`
8. controller Spring corrispondente nel backend
9. dashboard cliente
10. dashboard staff

Il codice contiene commenti numerati proprio per consentire di seguire questo percorso senza dover interpretare tutto insieme.
