# Mini design system — Grand Hotel Minerva

Questa pagina spiega le scelte visive applicate al progetto e può essere usata come riferimento quando aggiungi nuovi componenti.

## 1. Strategia: token, non colori sparsi

La regola principale della nuova versione è questa:

```scss
.panel {
  color: var(--albergo-text);
  background: var(--albergo-surface-raised);
  border: 1px solid var(--albergo-border-soft);
}
```

In questo modo lo stesso componente funziona sia in light sia in dark mode.

## 2. Palette chiara

| Ruolo | Colore | Uso |
|---|---|---|
| Verde profondo | `#102a2b` | navbar/footer e identità |
| Verde hotel | `#183f3b` | CTA e accenti |
| Verde azione | `#355c4d` | bottoni/link principali |
| Crema | `#fffdf7` | sfondo principale |
| Superficie | `#f7f4ee` | sezioni secondarie |
| Crema caldo | `#f2e7d5` | callout e dettagli |
| Oro/bruno | `#7b4e13` | eyebrow, focus, accenti |
| Testo | `#212121` | corpo principale |
| Testo secondario | `#5a4632` | descrizioni |
| Errore | `#6b1f2a` | validazione e azioni distruttive |

## 3. Palette scura

| Ruolo | Colore | Uso |
|---|---|---|
| Sfondo | `#0b1616` | body |
| Superficie | `#10201f` | sezioni |
| Card rialzata | `#152a28` | card/form |
| Superficie calda | `#20352f` | callout |
| Testo | `#f7f1e7` | corpo |
| Testo secondario | `#dccfbf` | descrizioni |
| Accento verde | `#a9d8c5` | CTA/controlli |
| Accento oro | `#f0c982` | focus/eyebrow |
| Errore | `#ffb4bd` | testo/stati errore |

## 4. Contrasti principali

Rapporti calcolati sui colori pieni:

```text
LIGHT
#102a2b su #fffdf7  -> 14.87:1
#7b4e13 su #fffdf7  ->  7.02:1
#5a4632 su #fffdf7  ->  8.76:1
#ffffff su #183f3b  -> 11.58:1

DARK
#f7f1e7 su #0b1616  -> 16.39:1
#dccfbf su #0b1616  -> 12.03:1
#f7f1e7 su #152a28  -> 13.41:1
#f0c982 su #0b1616  -> 11.73:1
```

Le coppie principali superano `7:1`, ma una conformità WCAG completa non può essere certificata solo dalla palette: richiede un audit dell'intera esperienza (focus, zoom, screen reader, contenuti dinamici, errori, semantica, ecc.).

## 5. Tipografia

Titoli:

```css
'Playfair Display', Georgia, 'Times New Roman', serif
```

Corpo:

```css
Inter, system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif
```

Playfair rende l'identità più editoriale e hospitality; Inter mantiene leggibili form, tabelle e dashboard.

## 6. Raggi e ombre

Card principali:

```text
border-radius: circa 1.15rem - 1.75rem
```

In dark mode le ombre sono più profonde; in light mode più morbide. I valori sono definiti nei token `--albergo-shadow` e `--albergo-shadow-soft`.

## 7. Regola delle azioni

- `mat-flat-button` = azione primaria;
- `mat-stroked-button` = azione secondaria;
- rosso = solo cancellazione/errore;
- una pagina non dovrebbe avere cinque azioni tutte visivamente primarie.

## 8. Focus

In `styles.css`:

```css
:where(a, button, input, select, textarea, [tabindex]):focus-visible {
  outline: 3px solid var(--albergo-gold) !important;
  outline-offset: 3px;
}
```

Il focus non viene rimosso: è una parte dell'interfaccia, non un difetto estetico.

## 9. Errori form

Un campo errato usa contemporaneamente:

1. bordo rosso;
2. testo di errore;
3. messaggio specifico.

Il significato non dipende quindi solo dal colore.

## 10. Movimento

La hero usa movimento lento (`heroBreath`) e reveal progressivo. Tutto viene disattivato quando il sistema dell'utente richiede `prefers-reduced-motion`.

## 11. Eccezione SPA

La pagina SPA usa una palette locale scura `--spa-*` anche in light mode. È una scelta intenzionale: la SPA viene trattata come esperienza immersiva. In dark mode resta coerente senza invertire accidentalmente i suoi colori.
