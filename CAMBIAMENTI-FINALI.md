# Rifiniture finali — riepilogo modifiche

Questa versione riunisce l'ultima base completa del progetto con le rifiniture richieste su prenotazioni, SPA, validazione e accessibilità.

## Area personale cliente

- Ogni prenotazione collegata all'account mostra ora **Modifica prenotazione** e **Cancella prenotazione** affiancati.
- `Cancella prenotazione` chiede conferma e usa il backend per cancellare realmente la prenotazione dal database.
- La modifica del nominativo mantiene validazione obbligatoria e segnalazione in rosso.
- La cancellazione elimina prima gli eventuali collegamenti `prenotazione_servizio`, evitando vincoli residui.

Endpoint:
- `PATCH /dashboard/user/bookings/{id}/nominativo`
- `DELETE /dashboard/user/bookings/{id}`

## Prenotazione senza login

- Rimane possibile prenotare senza registrazione.
- Vengono richiesti solamente i dati dell'intestatario: nome, cognome, email e cellulare.
- Il numero totale degli ospiti deriva da **Adulti + Bambini**: non vengono richiesti dati personali degli accompagnatori.
- In fondo al flusso di prenotazione anonima è presente il messaggio:

  > Una prenotazione effettuata senza login non viene collegata a un account e non potrà essere modificata dall'Area personale. Per eventuali variazioni o cancellazioni sarà necessario contattare direttamente la struttura.

## Ricerca camere e validazione

La ricerca camere della collega è mantenuta, con:
- check-in;
- check-out;
- adulti;
- bambini;
- animali;
- Pass SPA;
- camere a scheda orizzontale con capienza e prezzo.

Sono stati aggiunti:
- `*` rosso sui campi obbligatori;
- bordo rosso sui campi invalidi;
- messaggio specifico sotto il campo;
- errore rosso sul check-out se non è successivo al check-in;
- controllo numero adulti/bambini.

## Validazione pagamento carta

I campi carta sono ora validati lato interfaccia prima della conferma:
- intestatario;
- numero carta (13–19 cifre);
- scadenza `MM/AA`;
- CVC/CVV (3–4 cifre).

I campi carta **non vengono inviati né salvati nel backend**: questa validazione riguarda soltanto l'interfaccia attuale del progetto.

## Gestionale Staff

Nella sezione Prenotazioni non sono più presenti azioni `Conferma/Riapri` come azioni principali.

Ogni riga offre:
- **Modifica**;
- **Cancella**.

`Modifica` permette di cambiare:
- nominativo;
- camera;
- numero persone;
- check-in;
- check-out;
- stato confermata/non confermata.

`Cancella` elimina realmente la prenotazione dal database e pulisce i collegamenti ai servizi; per prenotazioni associate a un ospite anonimo/reception, l'anagrafica ospite viene eliminata solo quando non è più usata da altre prenotazioni.

Resta inoltre disponibile **+ Prenotazione in struttura**, che crea una prenotazione `STRUTTURA` senza account cliente.

Endpoint principali:
- `POST /staff/bookings`
- `PATCH /staff/bookings/{id}`
- `DELETE /staff/bookings/{id}`

## Prenotazione SPA

Nel form SPA è stata eliminata la scelta relativa alla camera/soggiorno.
La camera viene già gestita nel flusso di prenotazione camere.

La SPA chiede soltanto i dati necessari al Pass SPA e, opzionalmente, i dati del pet.

Nota: la conferma SPA rimane, come nella feature originale della collega, principalmente lato frontend e non introduce in questa versione una nuova persistenza SPA nel database.

## Angular Material

Sono stati aggiunti e utilizzati:
- `@angular/material`;
- `@angular/cdk`;
- `MatButtonModule` nei principali flussi (login, registrazione, area cliente, staff, prenotazione e SPA);
- tema Material 3 in `src/material-theme.scss`.

## Palette hotel / SPA e accessibilità

Palette principale:
- verde hotel: `#355c4d`;
- verde profondo: `#102a2b`;
- crema: `#fffdf7`;
- oro/marrone: `#7b4e13`;
- testo principale: `#212121`;
- errore: `#6b1f2a`;
- successo: `#1f5a39`.

I principali abbinamenti testo/sfondo sono stati scelti con rapporto di contrasto almeno `7:1`, soglia WCAG AAA per testo normale. Esempi:
- `#355c4d` su `#fffdf7`: circa **7.40:1**;
- `#7b4e13` su `#fffdf7`: circa **7.02:1**;
- `#212121` su `#fffdf7`: circa **15.83:1**;
- `#6b1f2a` su `#fffdf7`: circa **11.13:1**.

Sono presenti inoltre:
- focus visibile da tastiera;
- altezza minima di 44px sui principali controlli/azioni;
- errori indicati con bordo + testo, non soltanto con il colore;
- supporto `prefers-reduced-motion`.

**Nota:** i colori principali sono orientati ai criteri AAA, ma la conformità WCAG AAA completa dell'intera applicazione richiede un audit finale di tutte le pagine, immagini, contenuti e flussi con strumenti dedicati e test da tastiera/screen reader.

## Database

Rimane incluso `DatabaseSchemaUpdater.java`, che all'avvio allinea lo schema delle prenotazioni per supportare:
- `fk_prenotazione_utente` nullable;
- `fk_prenotazione_ospite`;
- `numero_ospiti`;
- `origine`;
- origini `ONLINE_UTENTE`, `ONLINE_OSPITE`, `STRUTTURA`.

Non vengono introdotti DTO o `record` per le request/response: i controller continuano a utilizzare entità e `Map<String, Object>`.
