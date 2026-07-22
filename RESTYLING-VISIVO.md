# Restyling visivo applicato

## Obiettivo

Trasformare la UI da semplice interfaccia gestionale/prenotazione a una piccola esperienza hospitality, mantenendo leggibilità e accessibilità.

## Homepage

Nuova struttura:

- hero fotografica ad alto contrasto;
- frase di impatto: **“Rallenta. Respira. Sei arrivato.”**;
- CTA Material per Camere e SPA;
- introduzione editoriale;
- tre Material Card sui punti di forza;
- sezione Camere con immagine;
- sezione SPA con immagine;
- citazione emozionale;
- CTA finale.

## Navbar

- brand più riconoscibile;
- sottotitolo Boutique · Spa · Relax;
- stato attivo evidente;
- azioni Material;
- comportamento responsive mantenuto tramite Bootstrap.

## Footer

Da footer tecnico a chiusura editoriale, con frase di brand, navigazione e contatti.

## Camere

Senza cambiare il complesso flusso funzionale:

- card più eleganti;
- bordi e shadow coerenti;
- sidebar di ricerca più leggibile;
- scrollbar discreta;
- immagini con raggi coerenti;
- focus/errori mantenuti ad alto contrasto.

## Login / registrazione

- sfondo più morbido;
- card con accento hotel;
- selettore Cliente/Staff più leggibile;
- focus e target touch preservati.

## Accessibilità

Sono stati mantenuti/rafforzati:

- focus visibile da tastiera;
- contrasto elevato nei testi principali;
- overlay robusto sulla fotografia hero;
- errori con testo + bordo, non solo colore;
- target di almeno circa 44px per le azioni principali;
- supporto a `prefers-reduced-motion`;
- struttura HTML con heading e section semantiche.

La conformità WCAG AAA completa richiede comunque un audit pagina per pagina con contenuti/dati reali e tecnologie assistive: il CSS da solo non può certificarla.
