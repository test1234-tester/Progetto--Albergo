// import { Component, inject } from '@angular/core';
// import { RouterLink, RouterLinkActive } from '@angular/router';

// import { AuthService } from '../../core/services/auth.service';

// @Component({
//   selector: 'app-navbar',
//   standalone: true,
//   imports: [RouterLink, RouterLinkActive],
//   templateUrl: './navbar.component.html',
//   styleUrl: './navbar.component.scss'
// })
// export class NavbarComponent {
//   readonly authService = inject(AuthService);

//   logout(): void {
//     this.authService.logout();
//   }
// }


//Applicate modifiche per aggirare l'errore CORS attivando dati finti
import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { filter } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink],
template: `
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark px-3">
      <a class="navbar-brand" routerLink="/">Grand Hotel Minerva</a>
      
      <div class="navbar-nav ms-auto align-items-center">
        
        <!-- SEZIONE DINAMICA: Camere e SPA compaiono solo se NON siamo in Homepage -->
        @if (!isHomepage()) {
          <a class="nav-link" routerLink="/stanze">Camere</a>
          <a class="nav-link" routerLink="/spa">SPA</a>
        }

        <!-- 1. Mostra solo se l'utente è un ADMIN dello staff -->
        @if (authService.currentUser()?.ruolo === 'ADMIN') {
          <a class="nav-link text-warning fw-bold" routerLink="/admin/dashboard">Pannello Staff</a>
        }

        <!-- 2. Mostra se l'utente NON è loggato -->
        @if (!authService.isAuthenticated()) {
          <a class="nav-link" routerLink="/login">Accedi</a>
          <a class="nav-link" routerLink="/register">Registrati</a>
        } @else {
          <!-- 3. Mostra se l'utente è loggato (Cliente o Admin che sia) -->
          <span class="navbar-text me-2 text-white">Ciao, {{ authService.currentUser()?.nome }}</span>
          <button class="btn btn-outline-light btn-sm" (click)="authService.logout()">Esci</button>
        }
      </div>
    </nav>
  `
})
export class NavbarComponent {
  // Inietta il servizio: l'HTML si aggiornerà da solo grazie ai signals currentUser e isAuthenticated!
  readonly authService = inject(AuthService);
  private readonly router = inject(Router); //Iniettiamo il Router per tracciare la posizione

  // SIGNAL: Controlla se l'utente si trova in Homepage
  readonly isHomepage = signal(false);

constructor() {
    // Ascolta i cambi di pagina usando il tipo flessibile 'any' per evitare bug di compilazione
    this.router.events.subscribe((event: any) => {
      // Se l'evento contiene l'URL finale della navigazione
      if (event.urlAfterRedirects) {
        // Aggiorna il signal: true se siamo in home, false altrimenti
        const url = event.urlAfterRedirects;
        this.isHomepage.set(url === '/' || url === '/home');
      }
    });
  }
}