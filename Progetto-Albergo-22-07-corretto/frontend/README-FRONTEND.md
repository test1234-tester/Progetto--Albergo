# Frontend aggiornato - Progetto Albergo

Questa cartella contiene solo il frontend Angular dell'ultima versione convergente.

## Avvio da CMD

```cmd
cd /d C:\percorso\Progetto-Albergo-frontend-aggiornato
npm install
npm start
```

## Avvio da PowerShell

```powershell
Set-Location "C:\percorso\Progetto-Albergo-frontend-aggiornato"
npm.cmd install
npm.cmd start
```

Il frontend usa `proxy.conf.json` per inoltrare le chiamate `/api` al backend Spring Boot.
Il backend deve quindi essere avviato separatamente sulla porta configurata (normalmente 8080).

Sono esclusi `node_modules`, `.angular`, `dist`, sorgenti Java e file Maven.
