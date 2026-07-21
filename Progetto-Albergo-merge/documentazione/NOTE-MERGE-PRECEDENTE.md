# Merge gestionale utente

Questa cartella parte **integralmente** dal progetto ricevuto dai colleghi (`Progetto--Albergo-main`).

## Cosa è stato preservato

Tutti i file presenti nel progetto ricevuto sono rimasti al loro posto. In particolare, non sono stati sovrascritti i file nuovi dei colleghi relativi a:

- `src/app/user-settings/`
- `src/app/payment/`
- `src/app/rooms/`
- `src/app/spa/`
- `albergo-frontend/`
- `albergo-beckend/`

Il gestionale è stato aggiunto in cartelle separate, così da ridurre al minimo i conflitti.

## Nuovi file aggiunti

Frontend:

- `src/app/user-dashboard/user-dashboard.component.ts`
- `src/app/user-dashboard/user-dashboard.component.html`
- `src/app/user-dashboard/user-dashboard.component.scss`
- `src/app/core/models/user-dashboard.model.ts`
- `src/app/core/services/user-dashboard.service.ts`

Backend:

- `src/main/java/com/example/progettoalbergo/Controller/UserDashboardController.java`

## Soli file condivisi modificati

Sono state aggiunte esclusivamente righe di integrazione a:

- `src/app/app.routes.ts` — nuova rotta `/gestionale-utente`
- `src/app/layout/navbar/navbar.component.ts` — link “Area personale”
- `package.json` — `npm start` usa il proxy già esistente

## Funzioni disponibili

Dopo il login, l’utente può aprire:

`http://localhost:4200/gestionale-utente`

Il gestionale:

- legge il profilo dell’utente autenticato dal database;
- permette di aggiornare nome, cognome, username e cellulare;
- mostra solo le prenotazioni collegate all’ID contenuto nel JWT;
- permette di cambiare il nominativo solo sulle proprie prenotazioni;
- mostra camera, date, stato e totale stimato del soggiorno.

## Avvio

Backend: importa in STS il progetto Maven che si trova nella **radice** di questa cartella, dove è presente `pom.xml`.

Frontend, sempre dalla radice:

```cmd
npm install
npm start
```

La cartella contiene anche copie annidate (`albergo-frontend` e `albergo-beckend`) già presenti nel materiale ricevuto: non sono state rimosse né modificate. Per questa convergenza il progetto attivo è quello nella radice.

## Verifiche eseguite

- controllo TypeScript (`tsc --noEmit`): superato;
- controllo dei template Angular (`ngc --noEmit`): superato;
- controllo sintattico Java 17 del nuovo controller: superato;
- confronto con lo ZIP originale: nessun file originale rimosso; soltanto i 3 file condivisi elencati sopra sono stati modificati.

La build Angular completa non è stata eseguita nel contenitore perché qui è installato Node 22.16, mentre Angular 22 richiede almeno Node 22.22.3. La compilazione Maven completa non è stata possibile perché il contenitore non poteva scaricare Maven e le dipendenze. Questi due controlli finali vanno eseguiti sul PC del gruppo.
