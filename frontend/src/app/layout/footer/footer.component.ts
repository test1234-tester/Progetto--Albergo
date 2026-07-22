/**
 * GUIDA DIDATTICA DEL FILE
 * ---------------------------------------------------------------------------
 * Footer globale con link, contatti e anno corrente.
 * Leggi prima gli import, poi @Component/@Injectable e infine campi e metodi:
 * questa è la stessa sequenza con cui Angular compone il comportamento del file.
 */
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

/**
 * FOOTER COMPONENT
 * ---------------------------------------------------------------------------
 * Componente puramente presentazionale. È condiviso da tutte le pagine e
 * contiene navigazione secondaria, contatti e l'anno corrente calcolato in JS.
 */
@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.scss'
})
export class FooterComponent {
  /** BLOCCO DIDATTICO — Evita di aggiornare manualmente l'anno nel copyright. */
  readonly currentYear = new Date().getFullYear();
}
