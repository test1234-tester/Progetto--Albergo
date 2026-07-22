/**
 * GUIDA DIDATTICA DEL FILE
 * ---------------------------------------------------------------------------
 * Servizio globale per la modalità chiara/scura.
 *
 * RESPONSABILITÀ DEL SERVIZIO
 * 1. Leggere l'eventuale preferenza salvata nel browser.
 * 2. Se non esiste una preferenza salvata, usare il tema del sistema operativo.
 * 3. Applicare il tema all'elemento <html> tramite l'attributo data-theme.
 * 4. Esporre un signal reattivo che i componenti possono leggere nel template.
 * 5. Salvare la scelta manuale dell'utente in localStorage.
 *
 * Il servizio non contiene CSS: decide soltanto QUALE tema è attivo.
 * I colori veri e propri sono definiti nei token CSS di src/styles.css e
 * src/material-theme.scss.
 */
import { Injectable, computed, signal } from '@angular/core';

/** I soli due temi visivi supportati dal progetto. */
export type AppTheme = 'light' | 'dark';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  /** Chiave usata in localStorage: cambiarla perderebbe la preferenza già salvata. */
  private readonly storageKey = 'grand-hotel-theme';

  /** Stato reattivo interno: il template non può modificarlo direttamente. */
  private readonly currentTheme = signal<AppTheme>(this.getInitialTheme());

  /** Signal pubblico in sola lettura: utile per aria-label, testo e icona del toggle. */
  readonly theme = this.currentTheme.asReadonly();

  /** Valore derivato: true quando il tema scuro è attivo. */
  readonly isDark = computed(() => this.currentTheme() === 'dark');

  constructor() {
    // BLOCCO 1 — Applichiamo subito il tema iniziale, prima di qualunque interazione.
    this.applyTheme(this.currentTheme());
  }

  /**
   * BLOCCO 2 — Cambia tema alla pressione del pulsante nella navbar.
   * Il nuovo valore viene sia applicato alla pagina sia salvato nel browser.
   */
  toggleTheme(): void {
    const nextTheme: AppTheme = this.currentTheme() === 'dark' ? 'light' : 'dark';
    this.setTheme(nextTheme);
  }

  /**
   * BLOCCO 3 — Impostazione esplicita del tema.
   * È separata da toggleTheme() per rendere il servizio riutilizzabile in futuro
   * anche con radio button, select o una pagina Preferenze.
   */
  setTheme(theme: AppTheme): void {
    this.currentTheme.set(theme);
    this.applyTheme(theme);

    if (typeof window !== 'undefined') {
      window.localStorage.setItem(this.storageKey, theme);
    }
  }

  /**
   * BLOCCO 4 — Decide il tema iniziale.
   * Priorità: preferenza salvata > preferenza del sistema > modalità chiara.
   */
  private getInitialTheme(): AppTheme {
    if (typeof window === 'undefined') return 'light';

    const savedTheme = window.localStorage.getItem(this.storageKey);
    if (savedTheme === 'light' || savedTheme === 'dark') return savedTheme;

    return window.matchMedia?.('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
  }

  /**
   * BLOCCO 5 — Collega lo stato Angular al CSS.
   * Impostando data-theme="dark" su <html>, tutte le variabili CSS dark diventano
   * immediatamente attive senza dover ricaricare i componenti.
   */
  private applyTheme(theme: AppTheme): void {
    if (typeof document === 'undefined') return;

    document.documentElement.dataset['theme'] = theme;
    document.documentElement.style.colorScheme = theme;
  }
}
