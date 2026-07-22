# Integrazione pagina SPA

Base utilizzata: `Progetto-Albergo-prenotazioni-complete`.

## Integrato dal branch `feature/pagina-spa`

Sono stati sostituiti esclusivamente i tre file della pagina SPA:

- `frontend/src/app/spa/spa.component.ts`
- `frontend/src/app/spa/spa.component.html`
- `frontend/src/app/spa/spa.component.scss`

Sono state inoltre copiate in `frontend/public/` le immagini usate dalla pagina SPA:

- 6 immagini SPA ospiti umani
- 4 immagini Pet SPA

## Funzioni presenti nella nuova pagina SPA

- Pass All-Inclusive da 200 €
- sezione SPA ospiti umani
- sezione Pet SPA
- carosello dei servizi
- miniature selezionabili
- navigazione precedente/successivo
- video-tour automatico al passaggio del mouse
- dettaglio durata, temperatura e caratteristiche
- modale di prenotazione
- scelta tra abbinamento al soggiorno e SPA-only
- dati opzionali dell'animale
- schermata di conferma

## Cosa è rimasto invariato

Non sono state modificate le funzioni già presenti nel progetto più recente:

- login Cliente / Staff
- JWT e ruoli
- area personale
- gestionale staff
- pagina camere della collega
- prenotazione con account
- prenotazione online senza account
- numero ospiti senza dati personali degli accompagnatori
- prenotazione inserita fisicamente dalla reception
- backend senza DTO

## Nota sulla prenotazione SPA

La modale SPA proviene dal branch della collega e, allo stato attuale, conferma la prenotazione nel frontend. Non è stata trasformata in un nuovo flusso persistente nel database, per evitare di alterare il lavoro SPA ricevuto senza una richiesta esplicita.

## Icone

La pagina originale usa classi Font Awesome ma il progetto non include Font Awesome. Sono stati aggiunti fallback CSS locali per le icone principali, così la pagina resta leggibile anche senza CDN o nuove dipendenze npm.
