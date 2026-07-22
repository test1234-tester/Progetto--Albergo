# Cambiamenti prenotazione e validazione

## 1. Rimossa la sezione "Ospiti in arrivo"

La sezione che chiedeva nome e cognome di ogni partecipante è stata rimossa.
Il numero di persone viene già scelto nella ricerca delle camere (Adulti + Bambini), quindi non è necessario raccogliere dati personali degli accompagnatori.

### Utente autenticato
- il backend usa automaticamente nome e cognome dell'account come intestatario;
- il frontend invia soltanto `numeroOspiti` oltre a camera e date;
- la prenotazione resta collegata all'utente e compare nell'Area personale.

### Utente senza account
- vengono chiesti soltanto i dati dell'intestatario: nome, cognome, email, cellulare;
- il numero totale di persone arriva dalla ricerca camere;
- non vengono chiesti dati degli altri partecipanti.

## 2. Campi obbligatori

Aggiunto `*` rosso accanto a tutti i campi obbligatori del form ospite e al trattamento.

## 3. Validazione campo per campo

I campi non validi vengono evidenziati con bordo rosso e messaggio specifico:
- Nome obbligatorio e minimo 2 caratteri
- Cognome obbligatorio e minimo 2 caratteri
- Email obbligatoria e formato email valido
- Cellulare obbligatorio e formato telefono valido

## 4. Errori restituiti dal backend

Per errori HTTP 400 il frontend:
- marca il form come touched;
- evidenzia i campi invalidi;
- se il messaggio del backend nomina un campo specifico, applica anche un errore server a quel campo.

Gli errori 409 (camera occupata) e 401/403 (sessione) restano mostrati come messaggi generali perché non dipendono da un singolo campo.

## 5. Backend prenotazione autenticata

`POST /prenotazioni` non riceve più una lista di ospiti.
Riceve:

```json
{
  "roomId": 1,
  "checkIn": "2026-08-10",
  "checkOut": "2026-08-12",
  "numeroOspiti": 2
}
```

Il backend recupera l'utente dal JWT/database e usa nome + cognome come `nominativo`.

## DTO

Non sono stati aggiunti DTO o record. Gli endpoint continuano a usare `Map<String, Object>`.

## 6. Corretto anche il flusso integrato nella pagina Stanze

La schermata mostrata nella pagina `rooms` della collega aveva un secondo form separato chiamato "Ospiti in arrivo". Anche quello è stato rimosso.

Ora, sia nel form dedicato sia nel dettaglio camera integrato nella pagina Stanze:
- viene mostrato soltanto il numero totale di persone;
- un utente autenticato non deve compilare nomi/cognomi degli accompagnatori;
- per un ospite anonimo sono obbligatori soltanto i dati dell'intestatario;
- la richiesta `POST /prenotazioni` invia `numeroOspiti` anziché una lista `guests`.
