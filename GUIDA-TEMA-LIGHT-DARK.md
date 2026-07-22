# Come funziona il tema Light / Dark

## 1. Il concetto più importante: design token

In `frontend/src/styles.css` non impostiamo lo stesso colore in cento componenti. Definiamo variabili globali:

```css
html[data-theme='light'] {
  --albergo-bg: #fffdf7;
  --albergo-text: #212121;
  --albergo-surface-raised: #ffffff;
}

html[data-theme='dark'] {
  --albergo-bg: #0b1616;
  --albergo-text: #f7f1e7;
  --albergo-surface-raised: #152a28;
}
```

Un componente scrive poi soltanto:

```scss
.card {
  color: var(--albergo-text);
  background: var(--albergo-surface-raised);
}
```

Quando cambia `data-theme`, la card cambia automaticamente.

## 2. ThemeService

File:

`frontend/src/app/core/services/theme.service.ts`

Il servizio:

1. legge la preferenza salvata;
2. altrimenti legge il tema del sistema;
3. applica `data-theme="light"` oppure `data-theme="dark"` a `<html>`;
4. espone `isDark()` alla navbar;
5. salva la scelta in `localStorage`.

## 3. Toggle nella navbar

Nel template della navbar trovi:

```html
<button
  mat-stroked-button
  type="button"
  class="theme-toggle"
  (click)="toggleTheme()"
  [attr.aria-pressed]="themeService.isDark()"
>
  {{ themeService.isDark() ? '☀' : '☾' }}
  {{ themeService.isDark() ? 'Chiaro' : 'Scuro' }}
</button>
```

`aria-pressed` comunica anche lo stato del controllo alle tecnologie assistive.

## 4. Perché c'è anche uno script in index.html?

Angular deve prima avviarsi prima di creare la navbar. Senza lo script iniziale, un utente dark potrebbe vedere per una frazione di secondo il tema light.

Lo script applica il tema prima del rendering iniziale; ThemeService prende poi il controllo durante l'esecuzione dell'app.

## 5. Angular Material

`src/material-theme.scss` contiene gli stessi due stati light/dark per i token `--mat-sys-*`.

Questo significa che Material e CSS personalizzato non sono due mondi separati: pulsanti, card e controlli Material seguono la stessa identità visiva del progetto.

## 6. Come aggiungere un nuovo componente compatibile

Preferisci:

```scss
.my-panel {
  color: var(--albergo-text);
  background: var(--albergo-surface-raised);
  border: 1px solid var(--albergo-border-soft);
}
```

invece di:

```scss
.my-panel {
  color: #212121;
  background: #ffffff;
}
```

Nel primo caso il dark mode funziona senza aggiungere altre regole.
