# Correzione area cliente e gestionale staff

## Cause individuate

1. Il form di prenotazione usava un `setTimeout` e generava l'ID finto `999`: nessuna prenotazione veniva salvata nel database.
2. Il campo `ruolo` esisteva soltanto nel frontend. Il backend autenticava ogni account nella tabella `utente`, quindi un presunto ADMIN era in realtà un normale cliente.
3. Non esistevano una rotta, un componente e un endpoint staff separati e protetti.

## Correzioni applicate

- `POST /prenotazioni` salva una prenotazione reale e ricava il cliente dal JWT.
- Controllo delle sovrapposizioni di date per la stessa camera.
- JWT con ruolo `CLIENTE` o `STAFF`.
- Login Cliente e login Staff separati.
- Registrazione pubblica riservata ai clienti.
- Account staff demo creato nella tabella `admin`.
- `/area-personale` mostra soltanto le prenotazioni del cliente autenticato.
- `/staff` mostra tutte le prenotazioni, le camere e i clienti.
- Guardie Angular e controlli backend impediscono lo scambio fra le due aree.
- Il gestionale staff può confermare le prenotazioni e modificare lo stato delle camere.
- Compatibilità con vecchie password in chiaro: al primo login corretto vengono migrate a BCrypt.

## File dei colleghi

Le pagine Camere, SPA, homepage, immagini e il prototipo `user-settings` sono stati conservati. Il prototipo resta disponibile su `/area-personale-demo`, mentre la rotta principale usa dati reali.

## Controlli effettuati

- build Angular development: superata;
- build Angular production: superata con un warning non bloccante sul budget del bundle;
- npm audit: 0 vulnerabilità;
- analisi sintattica di tutti i 63 file Java: nessun errore sintattico.

La compilazione Maven completa non è stata eseguita nel container perché Maven non era installato e il wrapper non poteva scaricare la distribuzione. STS eseguirà la compilazione semantica completa tramite `Maven > Update Project`.
