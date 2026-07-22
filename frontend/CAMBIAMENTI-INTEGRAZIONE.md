# Aggiornamento: camere stile Booking + prenotazione senza account

Base utilizzata: versione corretta del 22/07, mantenendo area cliente, login staff e gestionale staff.

## Frontend

### Camere
- Ripristinata la visualizzazione "stile Booking" presente in una versione precedente del progetto.
- Ricerca con:
  - check-in;
  - check-out;
  - numero adulti;
  - numero bambini.
- Le camere sono mostrate come schede orizzontali con foto, prezzo, capienza, comfort e totale indicativo.
- Il catalogo continua a essere letto dal backend `/camera`; non sono state reinserite le 50 camere simulate della vecchia versione.
- I parametri di ricerca vengono passati al form di prenotazione.

### Prenotazione senza login
- La rotta `/stanze/:id/prenota` non richiede più autenticazione.
- Se il cliente è autenticato, la prenotazione continua a essere associata al suo account.
- Se non è autenticato, il form richiede nome, cognome, email e cellulare e salva la persona come `Ospite`.
- La pagina di pagamento è raggiungibile anche senza login (il pagamento resta la simulazione già presente nel progetto).
- Il numero iniziale di campi "Ospite" viene ricavato da adulti + bambini scelti nella ricerca.

### Password
- Aggiunto il pulsante 👁️/🙈 al login.
- Aggiunto il pulsante 👁️/🙈 a password e conferma password nella registrazione.

## Backend

### Nessun DTO
- Non sono stati introdotti DTO, `record` Request/Response o package DTO.
- Il nuovo endpoint riceve i dati tramite `Map<String, Object>` come il resto del backend senza DTO.

### Prenotazioni ospite
- `PrenotazioneAlbergo` ha il nuovo campo nullable `fk_prenotazione_ospite`.
- Nuovo endpoint pubblico:

  `POST /prenotazioni/ospite`

- L'endpoint:
  1. valida camera e date;
  2. controlla la sovrapposizione delle prenotazioni;
  3. salva i dati nella tabella `ospite`;
  4. crea `prenotazione_albergo` con `fk_prenotazione_utente = null` e `fk_prenotazione_ospite` valorizzato.
- Le prenotazioni senza account non compaiono nell'Area personale di un utente registrato.
- Il gestionale staff riesce a mostrare nome ed email anche per le prenotazioni fatte come ospite.

## Non aggiunto
- Prenotazione inserita manualmente dalla reception/staff: **non implementata in questa versione**.
- Nessun nuovo DTO.
- Nessun campo `origine` o altra modifica non necessaria al modello.

## Nota database
Con `spring.jpa.hibernate.ddl-auto=update`, Hibernate aggiunge la nuova colonna nullable `fk_prenotazione_ospite` alla tabella `prenotazione_albergo` quando avvii il backend aggiornato.
