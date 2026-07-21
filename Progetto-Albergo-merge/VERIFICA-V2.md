# Verifica rapida v2

## 1. Backend

- STS deve mostrare `Started ProgettoalbergoApplication`.
- `http://localhost:8080/health` deve rispondere con `UP`.

## 2. Frontend

Avviare soltanto `frontend` con `npm start` o `npm.cmd start`.

## 3. Cliente

- login nell'area Cliente;
- nuova prenotazione;
- controllo tabella `prenotazione_albergo`;
- controllo pagina `/area-personale`.

## 4. Staff

- login nell'area Staff;
- apertura `/staff`;
- visualizzazione di tutte le prenotazioni;
- prova del pulsante Conferma;
- prova dello stato Libera/Occupata di una camera.

## Diagnostica

- `401`: token assente o scaduto; rifare il login.
- `403`: è stato scelto il tipo di accesso sbagliato.
- `404`: backend diverso o non aggiornato.
- `409` durante la prenotazione: camera già occupata nel periodo scelto.
