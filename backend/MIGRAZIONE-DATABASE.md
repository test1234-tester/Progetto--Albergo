# Migrazione automatica del database

Questa versione del backend contiene:

`src/main/java/com/example/progettoalbergo/Config/DatabaseSchemaUpdater.java`

Il componente viene eseguito automaticamente a ogni avvio di Spring Boot e controlla lo schema MySQL esistente senza cancellare i dati.

## Modifiche applicate automaticamente

Alla tabella `prenotazione_albergo`:

- `fk_prenotazione_utente` viene resa nullable;
- viene aggiunta `fk_prenotazione_ospite` se manca;
- viene aggiunta `numero_ospiti` se manca;
- viene aggiunta `origine` se manca;
- le vecchie prenotazioni ricevono `numero_ospiti = 1` se il valore non e' valido;
- le vecchie prenotazioni vengono classificate come `ONLINE_UTENTE` quando hanno un utente;
- viene creata la foreign key da `fk_prenotazione_ospite` a `ospite.idospite` se manca.

Inoltre verifica che:

- `ospite.idospite` sia `AUTO_INCREMENT`;
- `prenotazione_albergo.idprenotazione_albergo` sia `AUTO_INCREMENT`.

## Perche' serve

`spring.jpa.hibernate.ddl-auto=update` e' utile per lo sviluppo, ma non sempre modifica correttamente vincoli gia' esistenti, soprattutto un vecchio `NOT NULL`.

La prenotazione anonima deve poter salvare:

```text
fk_prenotazione_utente = NULL
fk_prenotazione_ospite = <id ospite>
numero_ospiti = <numero persone>
origine = ONLINE_OSPITE
```

mentre una prenotazione con account continua a usare:

```text
fk_prenotazione_utente = <id utente>
fk_prenotazione_ospite = NULL
origine = ONLINE_UTENTE
```

La reception usa invece `origine = STRUTTURA`.

## Prima del primo avvio

E' consigliato esportare un backup del database da MySQL Workbench.

Poi avviare Spring Boot normalmente. Nella console dovrebbero apparire messaggi simili a:

```text
Controllo schema database 'albergo' per le prenotazioni...
Modificata prenotazione_albergo.fk_prenotazione_utente: ora accetta NULL.
Aggiunta colonna prenotazione_albergo.fk_prenotazione_ospite.
Schema prenotazioni allineato correttamente.
```

Le operazioni sono idempotenti: agli avvii successivi il componente controlla prima di modificare lo schema.

## Controllo da Workbench

Dopo il primo avvio:

```sql
USE albergo;
DESCRIBE prenotazione_albergo;
```

`fk_prenotazione_utente` deve avere `Null = YES` e devono esistere:

- `fk_prenotazione_ospite`
- `numero_ospiti`
- `origine`

Per verificare la foreign key:

```sql
SELECT CONSTRAINT_NAME, COLUMN_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'albergo'
  AND TABLE_NAME = 'prenotazione_albergo'
  AND REFERENCED_TABLE_NAME IS NOT NULL;
```

## Nessun DTO

Questa modifica non introduce DTO o `record`. Il componente si occupa esclusivamente dello schema del database.
