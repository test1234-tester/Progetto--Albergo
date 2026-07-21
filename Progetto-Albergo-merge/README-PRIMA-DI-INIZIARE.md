# Progetto Albergo pulito v2

Questa versione contiene **un solo frontend Angular** e **un solo backend Spring Boot**.

```text
Progetto-Albergo-pulito-v2
├── backend
├── frontend
├── AVVIA-FRONTEND.cmd
└── README-PRIMA-DI-INIZIARE.md
```

## Cosa è stato corretto

- Le prenotazioni delle camere non sono più simulate: vengono salvate nella tabella `prenotazione_albergo`.
- L'utente della prenotazione viene ricavato dal JWT, non da un ID scelto dal browser.
- Area cliente e gestionale staff sono due pagine e due autorizzazioni diverse.
- Gli account cliente usano la tabella `utente`; lo staff usa la tabella `admin`.
- La registrazione crea soltanto account cliente. Gli account staff non si registrano dal sito pubblico.
- Il gestionale staff mostra tutte le prenotazioni, le camere e i clienti e permette di aggiornare stato prenotazione e camera.

## Backend in STS

Importa esclusivamente:

```text
C:\Progetto-Albergo-pulito-v2\backend
```

Usa `File > Import > Existing Maven Projects`, aggiorna Maven e avvia `ProgettoalbergoApplication`.

Controlla le credenziali MySQL in:

```text
backend\src\main\resources\application.properties
```

Verifica poi:

```text
http://localhost:8080/health
```

## Frontend

Da PowerShell:

```powershell
Set-Location "C:\Progetto-Albergo-pulito-v2\frontend"
npm.cmd install
npm.cmd start
```

Oppure fai doppio clic su `AVVIA-FRONTEND.cmd`.

Apri:

```text
http://localhost:4200
```

## Importante dopo il passaggio alla v2

I vecchi token non contengono il ruolo. Premi **Esci** e rifai il login. Se la barra continua a mostrare una vecchia sessione, apri gli strumenti del browser e cancella le chiavi `albergo_token` e `albergo_user` dal Local Storage.

## Prova cliente

1. Accedi selezionando **Cliente**.
2. Scegli una camera e date valide.
3. Compila gli ospiti e conferma la prenotazione.
4. Apri `/area-personale`.
5. La camera deve comparire in **Le mie camere prenotate**.
6. In Workbench puoi verificare con:

```sql
SELECT * FROM prenotazione_albergo ORDER BY idprenotazione_albergo DESC;
```

Le vecchie prenotazioni simulate con ID `999` non possono comparire, perché non erano mai state salvate nel database.

## Prova staff

Nel login seleziona **Staff** e usa l'account demo creato automaticamente al primo avvio:

```text
Email: reception@albergo.it
Password: Staff123!
```

La destinazione è:

```text
http://localhost:4200/staff
```

Il cliente non può aprire `/staff`; lo staff non viene inviato nell'area personale cliente.

## Stato del pagamento

La creazione della prenotazione è reale. La pagina di pagamento dei colleghi è ancora una simulazione grafica e non collega ancora un record `pagamento` alla prenotazione. Questo non impedisce di vedere la camera nell'area personale o nel gestionale staff.
