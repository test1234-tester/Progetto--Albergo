# Avvio del progetto — versione commentata + restyling + light/dark

## 1. Backend

In Spring Tool Suite importa **soltanto**:

```text
backend
```

come **Existing Maven Project** e avvia:

```text
ProgettoalbergoApplication
```

Il backend usa MySQL secondo:

```text
backend/src/main/resources/application.properties
```

Controllo rapido:

```text
http://localhost:8080/health
```

Al primo avvio `DatabaseSchemaUpdater` controlla/allinea le colonne necessarie alle prenotazioni anonime e in struttura. Prima di modifiche strutturali al DB è sempre consigliato un export di backup.

## 2. Frontend

PowerShell:

```powershell
Set-Location "C:\percorso\Progetto-Albergo-commentato-theme-light-dark\frontend"
npm.cmd install
npm.cmd start
```

Apri:

```text
http://localhost:4200
```

Lo script `start` usa automaticamente:

```text
ng serve --proxy-config proxy.conf.json
```

### Nota Node.js

Il progetto usa Angular 22. Se `npm install` mostra un errore `EBADENGINE`, aggiorna Node a una versione supportata dalla release Angular indicata nel messaggio npm. Non ignorare un errore di engine durante la preparazione dell'ambiente.

## 3. Sessione

Dopo modifiche all'autenticazione fai logout e nuovo login per rigenerare il JWT.

Account staff demo previsto dalla configurazione:

```text
reception@albergo.it
Staff123!
```

## 4. Angular Material

Material e CDK sono già nelle dipendenze:

```text
@angular/material
@angular/cdk
```

Tema:

```text
frontend/src/material-theme.scss
```

La Home e la Navbar mostrano esempi pratici di `MatButtonModule` e `MatCardModule`. La navbar contiene anche il toggle light/dark gestito da `ThemeService`.

## 5. Da dove iniziare a studiare

Ordine suggerito:

1. `GUIDA-CODICE-COMMENTATO.md`
2. `frontend/src/app/homepage/`
3. `GUIDA-TEMA-LIGHT-DARK.md` + `frontend/src/app/core/services/theme.service.ts`
4. `frontend/src/app/core/services/`
5. `frontend/src/app/rooms/`
6. `backend/.../Controller/`
7. `backend/.../Model/`
8. `GUIDA-ANGULAR-MATERIAL.md`

## 6. Documentazione inclusa

- `GUIDA-CODICE-COMMENTATO.md` — spiegazione del codice commentato.
- `MAPPA-COMPONENTI-E-BLOCCHI.md` — indice rapido di tutti i componenti frontend.
- `GUIDA-TEMA-LIGHT-DARK.md` — come funzionano ThemeService, token CSS e Material.
- `AGGIORNAMENTI-22-07-TEMA.md` — riepilogo degli ultimi cambiamenti.
- `GUIDA-ANGULAR-MATERIAL.md` — mini corso Material basato sul progetto.
- `RESTYLING-VISIVO.md` — cosa è cambiato nella UI e perché.
- `CAMBIAMENTI-FINALI.md` — riepilogo delle rifiniture funzionali precedenti.
- `CAMBIAMENTI-VALIDAZIONE.md` — modifiche su validazione e prenotazioni.
- `backend/MIGRAZIONE-DATABASE.md` — logica di allineamento DB.
- `frontend/CAMBIAMENTI-SPA.md` — integrazione pagina SPA.
