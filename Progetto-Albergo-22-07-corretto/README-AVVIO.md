# Avvio del progetto — rifiniture finali

## 1. Backend

In Spring Tool Suite importare **soltanto**:

```text
backend
```

come **Existing Maven Project** e avviare:

```text
ProgettoalbergoApplication
```

Il backend usa il database MySQL configurato in:

```text
backend/src/main/resources/application.properties
```

Controllo rapido:

```text
http://localhost:8080/health
```

Al primo avvio `DatabaseSchemaUpdater` controlla/allinea le colonne necessarie alle prenotazioni anonime e in struttura. È consigliato eseguire prima un backup del database.

## 2. Frontend

Da PowerShell, entrando nella cartella estratta:

```powershell
Set-Location "C:\percorso\Progetto-Albergo-rifiniture-finali-completo\frontend"
npm.cmd install
npm.cmd start
```

Aprire:

```text
http://localhost:4200
```

`npm start` usa automaticamente:

```text
ng serve --proxy-config proxy.conf.json
```

## 3. Sessione

Dopo aggiornamenti all'autenticazione fare logout e nuovo login per rigenerare il JWT.

Account staff demo previsto dalla configurazione:

```text
reception@albergo.it
Staff123!
```

## 4. Angular Material

Il frontend include Angular Material e CDK nelle dipendenze. Dopo aver estratto la cartella è quindi necessario eseguire almeno una volta:

```powershell
npm.cmd install
```

## 5. Documentazione

- `CAMBIAMENTI-FINALI.md`: riepilogo delle ultime rifiniture.
- `CAMBIAMENTI-VALIDAZIONE.md`: modifiche precedenti su prenotazione/validazione.
- `backend/MIGRAZIONE-DATABASE.md`: dettagli sulla migrazione automatica del DB.
- `frontend/CAMBIAMENTI-SPA.md`: integrazione della pagina SPA.
