# Avvio

## Backend

Importare in Spring Tool Suite come Existing Maven Project:

`backend`

Avviare `ProgettoalbergoApplication` come Spring Boot App.

Controllo database:

`http://localhost:8080/health`

## Frontend

Da PowerShell:

```powershell
Set-Location "C:\percorso\Progetto-Albergo-collega-ospite\frontend"
npm.cmd install
npm.cmd start
```

Aprire:

`http://localhost:4200`

Il comando `npm start` usa `proxy.conf.json` per inoltrare `/api` a Spring Boot sulla porta 8080.

## Staff demo

- Email: `reception@albergo.it`
- Password: `Staff123!`
