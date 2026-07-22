/**
 * GUIDA DIDATTICA DEL FILE
 * ---------------------------------------------------------------------------
 * Navbar globale con navigazione e contenuti condizionali in base alla sessione.
 * Leggi prima gli import, poi @Component/@Injectable e infine campi e metodi:
 * questa è la stessa sequenza con cui Angular compone il comportamento del file.
 */
import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';

import { AuthService } from '../../core/services/auth.service';
import { ThemeService } from '../../core/services/theme.service';

/**
 * NAVBAR COMPONENT
 * ---------------------------------------------------------------------------
 * La navbar è sempre visibile perché AppComponent la posiziona fuori dal
 * router-outlet. Legge AuthService per decidere quali link mostrare:
 * - visitatore: login / registrazione;
 * - cliente: Area personale;
 * - staff: Gestionale staff.
 */
@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, MatButtonModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent {
  /** inject() recupera l'istanza condivisa del servizio di autenticazione. */
  readonly authService = inject(AuthService);

  /**
   * BLOCCO DIDATTICO — Il ThemeService espone lo stato light/dark e il metodo
   * per cambiarlo. Lo teniamo pubblico perché il template legge isDark().
   */
  readonly themeService = inject(ThemeService);

  /**
   * BLOCCO DIDATTICO — Alterna modalità chiara e scura.
   * La preferenza viene memorizzata dal ThemeService in localStorage.
   */
  toggleTheme(): void {
    this.themeService.toggleTheme();
  }

  /** BLOCCO DIDATTICO — Pulisce la sessione locale delegando la logica ad AuthService. */
  logout(): void {
    this.authService.logout();
  }
}
