# Guida al codice commentato — Progetto Albergo

Questa copia del progetto è pensata anche come **materiale di studio**. I file sono stati commentati senza cambiare l'architettura principale: l'obiettivo è poter aprire un componente e capire rapidamente **chi chiama chi**, quali dati contiene e perché esiste ogni blocco.

## 1. La mappa mentale del progetto

```text
BROWSER / ANGULAR
      |
      |  HttpClient
      v
proxy.conf.json  ->  localhost:8080
      |
      v
SPRING CONTROLLER
      |
      v
SERVICE / REPOSITORY
      |
      v
JPA ENTITY
      |
      v
MYSQL
```

Quando premi, ad esempio, **Prenota**, il template chiama un metodo TypeScript. Quel metodo usa un service Angular. Il service invia HTTP al backend. Il controller Spring riceve la richiesta, usa repository/entity e infine MySQL salva o legge i dati.

---

# 2. Frontend Angular

## `src/main.ts`
È il vero punto di partenza del frontend. Avvia Angular e monta `App` usando la configurazione di `app.config.ts`.

## `src/app/app.ts`
È il componente radice. Non contiene logica di business: compone i tre pezzi sempre presenti:

- `NavbarComponent`
- `RouterOutlet`
- `FooterComponent`

Il `RouterOutlet` è il punto in cui Angular sostituisce dinamicamente Home, Camere, SPA, Login, Dashboard ecc.

## `src/app/app.routes.ts`
È la mappa URL -> pagina.

Esempi:

```text
/                    -> HomeComponent
/stanze              -> RoomsComponent
/spa                  -> SpaComponent
/area-personale      -> UserDashboardComponent
/staff                -> StaffDashboardComponent
```

Le route cliente/staff usano le guardie per evitare che un utente non autorizzato apra direttamente l'URL.

---

# 3. Layout condiviso

## `layout/navbar`
Responsabilità:

- mostra Home, Camere e SPA;
- mostra Area personale solo al cliente;
- mostra Gestionale staff solo allo staff;
- cambia Login/Registrazione in saluto/Logout dopo l'accesso;
- usa `AuthService` come unica fonte dello stato della sessione.

Nel restyling la navbar usa anche `MatButtonModule` per le azioni Accedi/Registrati/Esci.

## `layout/footer`
È puramente presentazionale. Contiene:

- descrizione della struttura;
- navigazione secondaria;
- contatti;
- anno corrente calcolato automaticamente.

---

# 4. Homepage

## `homepage/home.component.ts`
Non chiama il backend. Contiene soltanto l'array `highlights`, usato per mostrare tre card senza duplicare HTML.

Concetto importante:

```ts
@for (item of highlights; track item.title) { ... }
```

Angular ripete la stessa `mat-card` per ogni elemento dell'array.

## `homepage/home.component.html`
È divisa in blocchi semantici:

1. **Hero** — immagine, frase di impatto e CTA.
2. **Intro** — posizionamento emozionale.
3. **Highlights** — tre `MatCard`.
4. **Camere** — blocco editoriale con immagine e CTA.
5. **SPA** — blocco editoriale separato dal flusso camera.
6. **Quote** — pausa visiva.
7. **CTA finale** — riporta alla ricerca camere.

## Material usato nella Home

```ts
MatButtonModule
MatCardModule
```

Esempio:

```html
<a mat-flat-button routerLink="/stanze">Scopri le camere</a>
```

`routerLink` decide dove andare; `mat-flat-button` decide comportamento/stile Material.

---

# 5. Autenticazione

## `auth/login/login.component.ts`
Blocchi principali:

- `FormBuilder` crea il Reactive Form;
- `area` distingue CLIENTE e STAFF;
- `showPassword` è un signal booleano;
- `onSubmit()` valida e chiama `AuthService`;
- il redirect finale dipende dal ruolo.

## `auth/register/register.component.ts`
Gestisce:

- nome;
- cognome;
- username;
- cellulare;
- email;
- password;
- conferma password;
- validatori Angular;
- chiamata di registrazione.

La registrazione pubblica crea soltanto **CLIENTE**. Lo staff non viene creato da questo form.

---

# 6. Core: modelli, servizi, guardie e interceptor

## `core/models`
Questi file **non salvano niente** e non fanno richieste HTTP. Sono interfacce TypeScript che descrivono la forma dei dati.

Esempio concettuale:

```ts
interface Room {
  id: number;
  prezzo: number;
}
```

Significa: quando il frontend parla di una `Room`, TypeScript si aspetta almeno quella struttura.

## `core/services/auth.service.ts`
È il centro dell'autenticazione frontend:

- login cliente;
- login staff;
- registrazione;
- salvataggio/lettura JWT;
- utente corrente;
- ruolo;
- logout.

## `booking.service.ts`
Isola le chiamate HTTP delle prenotazioni. Il componente non deve conoscere ogni dettaglio dell'URL backend.

## `room.service.ts`
Recupera camere e disponibilità.

## `pricing.service.ts`
Non fa HTTP. Contiene calcoli riutilizzabili come numero di notti e prezzo.

## `user-dashboard.service.ts`
È il ponte tra Area personale e `/dashboard/user`.

## `staff-dashboard.service.ts`
È il ponte tra Gestionale e `/staff/...`.

## `auth.interceptor.ts`
Intercetta le richieste HTTP. Quando esiste un token JWT lo aggiunge all'header, evitando di scrivere la stessa logica in ogni service.

## `auth.guard.ts` / `role.guard.ts`
Controllano una route **prima** che Angular la apra.

---

# 7. Camere e prenotazione

## `rooms/rooms.component.ts`
È uno dei componenti più ricchi. I blocchi principali sono:

### Dipendenze

```ts
FormBuilder
RoomService
BookingService
AuthService
```

### Signal di stato

Esempi:

```ts
isLoading
selectedRoom
filteredRooms
prenotazioneConfermata
```

Un `signal()` è uno stato reattivo. Quando cambia, Angular aggiorna automaticamente il template che lo legge.

### Reactive Forms

- `guestContactForm`: dati intestatario prenotazione anonima;
- `cardPaymentForm`: campi della carta per il flusso grafico di pagamento.

### `computed()`

- `notti`;
- `costoTotale`;
- `caparra`;
- `saldoRimanente`.

Un `computed` viene ricalcolato solo quando cambiano i signal da cui dipende.

### Metodi principali

- `onSearch()` -> filtra le camere;
- `selezionaStanza()` -> apre il dettaglio;
- `toggleServizioOpzionale()` -> cambia il preventivo;
- `confermaEPay()` -> valida e crea la prenotazione;
- `isInvalid()` -> decide quando mostrare errore rosso.

## `rooms/booking/booking-form.component.ts`
È il flusso alternativo dedicato a `/stanze/:id/prenota`.

Distingue:

```text
CLIENTE LOGGATO
    -> userId dal JWT
    -> prenotazione visibile nell'Area personale

OSPITE ANONIMO
    -> nome/cognome/email/cellulare intestatario
    -> Ospite nel DB
    -> niente Area personale
```

---

# 8. Area personale cliente

## `user-dashboard/user-dashboard.component.ts`
Gestisce due aree:

### Profilo
`profileForm` permette la modifica dei dati personali consentiti.

### Prenotazioni
`futureBookings` e `confirmedBookings` derivano dai dati dashboard.

Azioni principali:

- `startBookingEdit()`;
- `saveBookingName()`;
- `cancelBookingEdit()`;
- `deleteBooking()`.

La cancellazione non modifica solo la pagina: chiama il backend che elimina la prenotazione nel DB.

---

# 9. Gestionale staff

## `staff-dashboard/staff-dashboard.component.ts`
È la vista amministrativa.

Signal importanti:

```text
activeSection
editingBookingId
creatingBooking
savingBooking
showPhysicalBookingForm
```

Form:

- `physicalBookingForm`: nuova prenotazione fatta in reception;
- `bookingEditForm`: modifica di una prenotazione esistente.

Azioni:

- crea prenotazione in struttura;
- modifica prenotazione;
- cancella prenotazione;
- cambia stato camera;
- mostra statistiche.

---

# 10. SPA

## `spa/spa.component.ts`
Gestisce soprattutto stato UI:

- categoria persone/pet;
- slide corrente;
- tour automatico;
- apertura/chiusura modale;
- dati del form SPA.

La scelta della camera è stata rimossa: camera e SPA restano due flussi separati.

> Nota: controllare sempre il controller SPA prima di considerare la prenotazione SPA completamente persistita. Parte del flusso originario era nato come simulazione frontend.

---

# 11. Pagamento

## `payment/payment.component.ts`
Legge dall'URL:

- ID prenotazione;
- importo totale.

Calcola:

```text
caparra = 10%
saldo = totale - caparra
```

Poi usa `PaymentService` per il flusso previsto dal backend.

---

# 12. Backend Spring Boot

Il backend non usa DTO aggiuntivi nella parte nuova: per payload compositi sono stati mantenuti oggetti/entity o `Map<String, Object>` dove previsto dal progetto.

## `Controller`
È il livello HTTP.

Controller importanti:

### `AuthController`

- `/auth/register`
- `/auth/login`
- `/auth/staff/login`

### `PrenotazioneAlbergoController`

- prenotazione cliente;
- prenotazione ospite;
- CRUD prenotazioni albergo.

### `UserDashboardController`

- legge dashboard cliente;
- modifica profilo;
- modifica nominativo prenotazione;
- cancella prenotazione del cliente.

### `StaffDashboardController`

- dashboard staff;
- prenotazione reception;
- modifica prenotazione;
- cancellazione;
- gestione stato camere.

### `HealthController`
Endpoint rapido usato per verificare che Spring e MySQL siano raggiungibili.

---

# 13. Entity JPA (`Model`)

Le entity rappresentano il database.

Esempi:

- `Utente` -> clienti registrati;
- `Ospite` -> intestatari non registrati;
- `Camera` -> camere;
- `PrenotazioneAlbergo` -> prenotazioni;
- `Pagamento` -> pagamenti;
- `Spa` / `PrenotazioneSpa` -> dominio wellness.

Annotazioni da riconoscere:

```java
@Entity
@Table
@Id
@GeneratedValue
@Column
@ManyToOne
@OneToMany
@JoinColumn
```

Sono istruzioni a JPA/Hibernate su come tradurre oggetti Java in righe/relazioni SQL.

---

# 14. Repository

Le interfacce in `Repository` delegano a Spring Data JPA il CRUD.

Mentalmente puoi leggerle così:

```text
Controller -> Repository -> MySQL
```

Spring genera a runtime gran parte dell'implementazione.

---

# 15. Services backend

Le classi `*Hib` raccolgono operazioni sulle relative entity. Sono nate nell'architettura del progetto come strato intermedio tra controller e repository.

---

# 16. Configurazione e database

## `Config/DataSeeder.java`
Inserisce dati demo necessari quando previsti, compreso l'account staff di sviluppo.

## `Config/DatabaseSchemaUpdater.java`
Serve ad allineare alcune colonne/vincoli della tabella prenotazioni con il modello più recente, in particolare il supporto alle prenotazioni senza account.

## `application.properties`
Contiene configurazione Spring/MySQL/Hibernate.

Non pubblicare mai password reali dentro un repository pubblico.

---

# 17. Sicurezza

## `Security/JwtUtil.java`
Crea e legge i token JWT.

Il JWT consente al backend di sapere chi sta facendo la richiesta senza inviare nuovamente email/password a ogni chiamata.

## `PasswordConfig.java`
Configura l'encoder usato per non salvare password in chiaro.

---

# 18. Come studiare un flusso reale

Prendi una funzione, per esempio **cancellazione prenotazione utente**, e segui sempre questo ordine:

```text
1. user-dashboard.component.html
      ↓ click
2. user-dashboard.component.ts
      ↓ deleteBooking()
3. user-dashboard.service.ts
      ↓ HTTP DELETE
4. UserDashboardController.java
      ↓ @DeleteMapping
5. PrenotazioneAlbergoRepository
      ↓
6. MySQL
```

Questo metodo è molto più efficace che cercare di imparare tutto il progetto contemporaneamente.

---

# 19. Legenda dei commenti inseriti

Nei file troverai diciture come:

```text
GUIDA DIDATTICA DEL FILE
GUIDA DIDATTICA TEMPLATE
GUIDA DIDATTICA STILI
GUIDA DIDATTICA BACKEND
BLOCCO DIDATTICO
```

Servono appositamente per distinguere i commenti di studio dai commenti tecnici già presenti nel progetto.
