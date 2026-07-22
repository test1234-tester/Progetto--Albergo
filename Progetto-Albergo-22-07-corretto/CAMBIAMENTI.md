# Progetto Albergo – versione camere collega + prenotazione ospite

Questa cartella nasce dalla versione aggiornata con area cliente/staff e backend senza DTO.
La pagina `Stanze` è stata riportata alla struttura grafica presente nel frontend della collega in:

`Progetto--Albergo-main(1) / albergo-frontend / src / app / rooms`

## Modifiche mantenute

- Login Cliente / Staff con JWT e ruoli.
- Area personale cliente.
- Gestionale staff.
- Collegamento MySQL/Spring Boot.
- Backend senza package DTO, senza `record` Request/Response.

## Pagina Stanze

È stata mantenuta la struttura della collega con:

- sidebar di ricerca;
- check-in e check-out;
- adulti e bambini;
- animali domestici;
- pacchetto SPA;
- schede orizzontali delle camere con foto, prezzo, capienza e comfort;
- dettaglio camera;
- servizi opzionali;
- riepilogo importi, caparra e saldo;
- scelta carta/bonifico e campo CVC con mostra/nascondi.

Le camere vengono lette dal backend `/camera`. Se il backend non risponde, il frontend mostra 50 camere di fallback per non lasciare vuota la pagina.

## Prenotazione senza registrazione

La prenotazione può essere effettuata anche senza login direttamente dalla pagina Stanze.

Per l'utente non autenticato vengono richiesti:

- nome;
- cognome;
- email;
- cellulare;
- nominativi degli ospiti in arrivo.

Il frontend chiama:

`POST /prenotazioni/ospite`

Il backend salva:

1. un record nella tabella `ospite`;
2. la prenotazione in `prenotazione_albergo`;
3. `fk_prenotazione_utente = NULL`;
4. `fk_prenotazione_ospite = id dell'ospite creato`.

Non vengono usati DTO: il controller riceve il JSON tramite `Map<String, Object>`.

Per un cliente autenticato viene invece usato:

`POST /prenotazioni`

ed è il JWT a determinare l'utente della prenotazione.

## Occhietto password

È presente mostra/nascondi password:

- nel login;
- nella password di registrazione;
- nella conferma password;
- nel CVC della schermata camere della collega.

## Non aggiunto

Non è stata inserita la funzione per creare manualmente una prenotazione fisica dalla dashboard staff/reception.
