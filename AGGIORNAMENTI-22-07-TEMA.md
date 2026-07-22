# Aggiornamenti grafici e didattici

Questa versione parte da **Progetto-Albergo-commentato-restyling** e aggiunge gli ultimi cambiamenti richiesti.

## Homepage

La hero ora usa i testi scelti insieme:

- **Dove tutto il resto si fa lontano.**
- *Uno spazio di quiete, acqua e benessere in cui lasciarti andare.*
- Sezione SPA: **Entra nella quiete.** / *Il tuo tempo. Il tuo spazio. Il tuo benessere.*
- CTA finale: **Smetti di pensare al tempo.** / *Da qui inizia la tua pausa dal mondo.*

L'immagine hero è molto più visibile: l'overlay globale è stato alleggerito e il contrasto del testo viene protetto con un gradiente radiale localizzato soltanto dietro al contenuto.

È stato aggiunto un leggerissimo effetto `heroBreath` e un ingresso progressivo dei testi. Entrambi vengono disattivati con `prefers-reduced-motion`.

## Light / Dark mode

È stato aggiunto:

- `core/services/theme.service.ts`;
- toggle **Chiaro / Scuro** nella navbar;
- salvataggio della scelta in `localStorage`;
- rilevamento automatico iniziale di `prefers-color-scheme`;
- script in `index.html` per evitare il flash del tema sbagliato all'avvio;
- token light/dark in `src/styles.css`;
- token Material light/dark in `src/material-theme.scss`.

La SPA mantiene intenzionalmente un'atmosfera scura anche nel tema chiaro, perché è trattata come esperienza immersiva separata. Usa token locali `--spa-*`, quindi non viene accidentalmente invertita dal tema globale.

## Camere

Corretto l'effetto delle card "sottilette". Le card dei risultati ora hanno:

- `min-height` reale;
- row interna con altezza minima;
- colonna immagine con dimensione garantita;
- immagine assoluta con `object-fit: cover`;
- adattamento mobile separato.

Il file principale è `frontend/src/app/rooms/rooms.component.scss`.

## Commenti didattici

I componenti frontend sono stati ulteriormente commentati con blocchi come:

- `BLOCCO DIDATTICO` nei TypeScript;
- `BLOCCO 1`, `BLOCCO 2...` nei template;
- blocchi numerati negli SCSS.

Sono stati commentati in modo particolarmente dettagliato homepage, navbar, camere, SPA, login/registrazione, dashboard cliente/staff, form prenotazione, room-card, footer e impostazioni utente.

## Verifica eseguita

È stato eseguito un controllo sintattico tramite TypeScript `transpileModule` su tutti i file `.ts` del frontend: **37 file, 0 errori sintattici**.

È stato inoltre eseguito un controllo di bilanciamento delle parentesi graffe su tutti i file CSS/SCSS: **0 mismatch**.

Il vero `npm install` / `ng build` non ha potuto essere completato nell'ambiente di generazione perché il registry npm interno ha restituito errori HTTP 503 durante il download delle dipendenze. Il controllo definitivo Angular va quindi eseguito sul PC locale con `npm.cmd install` e `npm.cmd start`.
