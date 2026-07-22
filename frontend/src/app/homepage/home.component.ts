/**
 * GUIDA DIDATTICA DEL FILE
 * ---------------------------------------------------------------------------
 * Componente Angular standalone: coordina template, stato e interazioni della pagina.
 * Leggi prima gli import, poi @Component/@Injectable e infine campi e metodi:
 * questa è la stessa sequenza con cui Angular compone il comportamento del file.
 */
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

// Angular Material: importiamo solo i moduli effettivamente usati nel template.
// In un progetto standalone non esiste un AppModule centrale: ogni componente
// dichiara in autonomia le proprie dipendenze nell'array `imports`.
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';

/**
 * HOME COMPONENT
 * ---------------------------------------------------------------------------
 * È la pagina di ingresso del sito. Qui non servono chiamate al backend:
 * il suo compito è presentare la struttura, comunicare l'atmosfera del soggiorno
 * e guidare l'utente verso le due azioni principali: camere e SPA.
 *
 * La logica resta volutamente minima: una homepage principalmente descrittiva
 * è più semplice da mantenere e più veloce da caricare.
 */
@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, MatButtonModule, MatCardModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent {
  /**
   * BLOCCO DIDATTICO — CONTENUTI DELLE CARD.
   * Piccoli contenuti dichiarativi usati dalle card dei punti di forza.
   * Tenerli nel TypeScript evita di ripetere markup e rende evidente come
   * Angular possa generare elementi HTML partendo da un array di dati.
   */
  readonly highlights = [
    {
      eyebrow: 'RIPOSO',
      title: 'Lascia fuori la fretta',
      description:
        'Spazi morbidi e silenziosi, pensati per farti dimenticare per un po’ ciò che ti aspetta fuori.'
    },
    {
      eyebrow: 'BENESSERE',
      title: 'Entra nella quiete',
      description:
        'Acqua, calore e silenzio: sensazioni semplici, da vivere senza pensare all’orologio.'
    },
    {
      eyebrow: 'LIBERTÀ',
      title: 'Scegli solo ciò che ti fa stare bene',
      description:
        'Camere, servizi ed esperienze da combinare con semplicità, seguendo il ritmo che preferisci.'
    }
  ];
}
