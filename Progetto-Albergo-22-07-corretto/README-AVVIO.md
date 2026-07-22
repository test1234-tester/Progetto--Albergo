# Avvio rapido

## 1. Backend

In Spring Tool Suite importa esclusivamente la cartella `backend` come Existing Maven Project.

Poi:

1. Maven -> Update Project
2. avvia `ProgettoalbergoApplication` come Spring Boot App
3. apri `http://localhost:8080/health`

Credenziali staff demo:

- `reception@albergo.it`
- `Staff123!`

## 2. Frontend

PowerShell:

```powershell
Set-Location "C:\percorso\Progetto-Albergo-22-07-corretto\frontend"
npm.cmd install
npm.cmd start
```

CMD:

```cmd
cd /d C:\percorso\Progetto-Albergo-22-07-corretto\frontend
npm install
npm start
```

Apri `http://localhost:4200`.

Se avevi già effettuato il login con una vecchia versione, fai logout e login di nuovo.
