# Progetto Albergo 22-07 — modifiche di allineamento

Questa versione parte dallo ZIP `progettoAlbergo-22-07-20260722T100530Z-1-001.zip` e mantiene separate le due cartelle:

- `frontend/` — Angular
- `backend/` — Spring Boot

## Vincoli rispettati

- Nessun DTO Java aggiunto.
- Rimossa la classe `AuthResponse.java` che svolgeva il ruolo di DTO.
- Nessun `record` Java usato come request/response.
- Le request composte vengono lette con `Map<String, ...>` / `Map<String, Object>`.
- Le response composte vengono costruite con `Map<String, Object>`.
- NON è stata aggiunta la prenotazione online senza registrazione.
- NON è stata aggiunta la prenotazione fisica inserita dallo staff.
- La prenotazione già esistente resta riservata al cliente autenticato.
- Il lavoro già presente su SPA, visite fisiche e altri CRUD non è stato eliminato.

---

# Frontend

## 1. Corretto `rooms.component.ts`

File:

`frontend/src/app/rooms/rooms.component.ts`

Problema originale: `RoomsComponent` veniva chiuso prima del metodo `bookingQueryParams()`. Il metodo rimaneva fuori dalla classe e il file non era TypeScript valido.

Correzione:

- sistemate le parentesi graffe;
- `bookingQueryParams()` è nuovamente dentro `RoomsComponent`;
- mantenuta la logica aggiunta dal gruppo per le tre tipologie di camera;
- mantenuto il fallback con 50 camere simulate quando il backend non è raggiungibile.

## 2. Eliminata la doppia navbar

File:

- `frontend/src/app/layout/navbar/navbar.component.ts`
- `frontend/src/app/layout/navbar/navbar.component.html`

Problema originale: il `.ts` conteneva un `template: \`...\`` inline, quindi Angular ignorava completamente `navbar.component.html` e relativo stile.

Correzione:

- la navbar usa ora `templateUrl: './navbar.component.html'`;
- usa `styleUrl: './navbar.component.scss'`;
- mantenuta la grafica `Albergo Online` presente nel lavoro del gruppo;
- aggiunto `RouterLinkActive`.

La navigazione ora cambia in base al ruolo:

Cliente:

- Home
- Stanze
- Spa
- Area personale
- Esci

Staff:

- Home
- Stanze
- Spa
- Gestionale staff
- Esci

Utente non autenticato:

- Home
- Stanze
- Spa
- Accedi
- Registrati

## 3. Guardie dei ruoli rese più sicure

File:

`frontend/src/app/core/guards/role.guard.ts`

Correzione:

- CLIENTE può accedere ad `/area-personale`;
- STAFF può accedere a `/staff`;
- un cliente che apre `/staff` viene portato ad `/area-personale`;
- uno staff che apre `/area-personale` viene portato a `/staff`;
- evitato il rimbalzo infinito fra le due rotte in caso di sessione con ruolo non riconosciuto.

## 4. Endpoint frontend mantenuti e ora allineati al backend

Il frontend già chiamava:

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/staff/login`
- `POST /api/prenotazioni`
- `GET /api/dashboard/user`
- `PATCH /api/dashboard/user/profile`
- `PATCH /api/dashboard/user/bookings/{id}/nominativo`
- `GET /api/staff/dashboard`
- `PATCH /api/staff/bookings/{id}/status`
- `PATCH /api/staff/rooms/{id}/status`

Questi endpoint sono ora presenti anche nel backend aggiornato.

Il proxy resta:

`/api -> http://localhost:8080`

Per questo il frontend va avviato con `npm start`, che usa `proxy.conf.json`.

---

# Backend

## 1. Autenticazione senza DTO

File modificato:

`backend/src/main/java/com/example/progettoalbergo/Controller/AuthController.java`

File rimosso:

`backend/src/main/java/com/example/progettoalbergo/Controller/AuthResponse.java`

Modifiche:

- registrazione cliente mantiene `@RequestBody Utente`;
- login cliente usa `@RequestBody Map<String, String>`;
- login staff usa `@RequestBody Map<String, String>`;
- le response vengono costruite con `Map<String, Object>`;
- il JSON restituito contiene `token` e `user`;
- `user` contiene sempre `role: CLIENTE` oppure `role: STAFF`;
- email cliente gestita senza distinzione fra maiuscole/minuscole;
- compatibilità con eventuali vecchie password salvate in chiaro: dopo un login valido vengono migrate a BCrypt.

## 2. JWT con ruolo

File:

`backend/src/main/java/com/example/progettoalbergo/Security/JwtUtil.java`

Il token ora contiene:

- `userId`
- `role`
- email nel subject

Aggiunto `extractRole()` per proteggere le aree CLIENTE e STAFF.

## 3. Login staff

Endpoint aggiunto:

`POST /auth/staff/login`

Repository aggiornato:

`backend/src/main/java/com/example/progettoalbergo/Repository/AdminRepository.java`

Il controllo email è case-insensitive.

## 4. Account staff demo

File:

`backend/src/main/java/com/example/progettoalbergo/Config/DataSeeder.java`

Se non esiste, all'avvio viene creato:

- email: `reception@albergo.it`
- password: `Staff123!`

La password viene salvata con BCrypt.

Il seeder continua inoltre a creare le camere soltanto quando la tabella camere è vuota.

## 5. Area personale cliente

Nuovo controller:

`backend/src/main/java/com/example/progettoalbergo/Controller/UserDashboardController.java`

Endpoint:

- `GET /dashboard/user`
- `PATCH /dashboard/user/profile`
- `PATCH /dashboard/user/bookings/{bookingId}/nominativo`

L'ID del cliente non viene accettato dal browser: viene letto dal JWT.

Il cliente può vedere solo le prenotazioni associate al proprio `idUtente` e non può modificare prenotazioni altrui.

## 6. Gestionale staff

Nuovo controller:

`backend/src/main/java/com/example/progettoalbergo/Controller/StaffDashboardController.java`

Endpoint:

- `GET /staff/dashboard`
- `PATCH /staff/bookings/{bookingId}/status`
- `PATCH /staff/rooms/{roomId}/status`

Restituisce:

- statistiche;
- prenotazioni;
- camere;
- utenti registrati.

Gli endpoint richiedono un JWT con ruolo `STAFF`.

## 7. Prenotazione online già esistente riallineata

File:

`backend/src/main/java/com/example/progettoalbergo/Controller/PrenotazioneAlbergoController.java`

Aggiunto l'endpoint che il frontend già utilizzava:

`POST /prenotazioni`

Funziona solo per un CLIENTE autenticato e legge il cliente dal JWT.

Non sono state aggiunte le due funzionalità future:

- prenotazione anonima;
- prenotazione inserita dalla reception.

I vecchi endpoint CRUD `/prenotazionealbergo` sono stati mantenuti per non interrompere altro codice del gruppo.

## 8. Repository allineati alle dashboard

Modificati:

- `AdminRepository.java`
- `UtenteRepository.java`
- `PrenotazioneAlbergoRepository.java`
- `PagamentoRepository.java`

Aggiunte soltanto query/metodi necessari per autenticazione e dashboard.

## 9. Endpoint di controllo database

Nuovo controller:

`HealthController.java`

Endpoint:

`GET http://localhost:8080/health`

Permette di verificare rapidamente che Spring Boot stia usando il database corretto.

## 10. MySQL ripulito

`pom.xml` conteneva due driver MySQL contemporaneamente.

È rimasto soltanto:

`com.mysql:mysql-connector-j`

Rimossi inoltre:

- `src/main/resources/templates/mysql-connector-java-5.1.47.jar`
- il riferimento assoluto a quel JAR dentro `.classpath`

Questo evita l'errore STS che cercava un file sotto un percorso appartenente a un altro PC.

---

# File del gruppo mantenuti

Non sono stati rimossi i componenti/modelli già presenti relativi a:

- SPA;
- `VisitaAlbergo`;
- `VisitaSpa`;
- `SpaVisitaSpa`;
- comfort;
- servizi;
- ospiti;
- CRUD già presenti.

La modifica è stata fatta sopra lo ZIP del 22-07, non sostituendo il backend con una vecchia copia completa.

---

# Funzionalità volutamente NON aggiunte

Questa consegna NON contiene ancora:

1. prenotazione online senza account/login;
2. prenotazione fisica creata dalla reception;
3. collegamento di una prenotazione anonima a `Ospite`;
4. campo `origine` (`ONLINE_UTENTE`, `ONLINE_OSPITE`, `STRUTTURA`).

Queste modifiche restano separate come richiesto.

Il pagamento mantiene il comportamento già presente nel progetto ricevuto; non è stato riscritto in questa correzione.

---

# Avvio

## Backend

Importare in Spring Tool Suite come **Existing Maven Project**:

`backend/`

Controllare `src/main/resources/application.properties` e poi avviare `ProgettoalbergoApplication`.

Test:

`http://localhost:8080/health`

## Frontend

Da PowerShell dentro `frontend/`:

```powershell
npm.cmd install
npm.cmd start
```

Da CMD:

```cmd
npm install
npm start
```

Aprire:

`http://localhost:4200`

Dopo aver sostituito una versione precedente, fare **Esci e nuovo login**: i vecchi token creati prima dell'introduzione del ruolo non contengono `CLIENTE`/`STAFF`.

---

# Verifiche eseguite

- Cercati file Java DTO/request/response: nessuno presente nella cartella backend aggiornata.
- Cercato uso di `record` Java: nessuno presente.
- Verificato che nel `pom.xml` resti un solo driver MySQL.
- Verificato che `.classpath` non contenga più il percorso assoluto del vecchio JAR MySQL.
- Eseguito controllo sintattico TypeScript sui sorgenti: nessun errore di parsing rilevato; nel runtime di preparazione non erano disponibili le dipendenze Angular, quindi non è stato possibile effettuare il build Angular completo.
- La compilazione Maven completa non è stata eseguita perché il Maven Wrapper nel runtime di preparazione non riusciva a scaricare Maven Central. Il controllo finale va quindi eseguito in STS con `Maven -> Update Project` e avvio Spring Boot.
