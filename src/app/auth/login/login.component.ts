// Importa le funzionalità core di Angular per creare il componente, iniettare servizi e usare i Signals[cite: 1]
import { Component, inject, signal } from '@angular/core';
// Importa gli strumenti dei Reactive Forms per la creazione dei controlli e la loro validazione[cite: 1]
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
// Importa i moduli per gestire la navigazione e leggere i parametri memorizzati nell'URL corrente[cite: 1]
import { Router, RouterLink, ActivatedRoute } from '@angular/router';

// Importa il servizio AuthService che contiene le chiamate HTTP di autenticazione verso il backend[cite: 1]
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',                 // Nome del tag HTML personalizzato associato a questo componente[cite: 1]
  standalone: true,                      // Componente standalone, autonomo e indipendente da moduli esterni[cite: 1]
  imports: [ReactiveFormsModule, RouterLink], // Moduli per i form e la navigazione abilitati in questo HTML[cite: 1]
  templateUrl: './login.component.html', // Collegamento al file dell'interfaccia grafica[cite: 1]
  styleUrl: './login.component.scss'     // Collegamento al file dei fogli di stile[cite: 1]
})
export class LoginComponent {
  // Inietta il costruttore di form per generare istanze di controlli reattivi[cite: 1]
  private readonly fb = inject(FormBuilder);
  // Inietta il servizio di autenticazione per gestire le chiamate di login al server[cite: 1]
  private readonly authService = inject(AuthService);
  // Inietta il router per gestire i reindirizzamenti di pagina nel browser[cite: 1]
  private readonly router = inject(Router);
  // Inietta la rotta attiva per leggere eventuali parametri passati in coda all'URL[cite: 1]
  private readonly route = inject(ActivatedRoute);

  // Inizializza il gruppo del form reattivo definendo i campi, i valori iniziali e le regole di convalida[cite: 1]
  readonly loginForm = this.fb.nonNullable.group({
    // Campo email: inizialmente vuoto, obbligatorio e deve rispettare il formato standard di una mail[cite: 1]
    email: ['', [Validators.required, Validators.email]],
    // Campo password: inizialmente vuoto, obbligatorio e con un vincolo minimo di 6 caratteri[cite: 1]
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  // Signal booleano per tracciare se c'è un invio HTTP in corso (disabilita i pulsanti)[cite: 1]
  readonly isSubmitting = signal(false);
  // Signal testuale per contenere e mostrare un eventuale messaggio d'errore restituito dal server[cite: 1]
  readonly errorMessage = signal<string | null>(null);

  // Metodo eseguito quando l'utente invia il form cliccando sul pulsante o premendo Invio[cite: 1]
  onSubmit(): void {
    // Controllo preventivo: se il form contiene errori di validazione, blocca l'operazione[cite: 1]
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched(); // Evidenzia graficamente i campi con errori non ancora corretti[cite: 1]
      return; // Interrompe l'esecuzione del metodo[cite: 1]
    }

    // Imposta lo stato di invio a true per mostrare l'animazione di caricamento o disabilitare la UI[cite: 1]
    this.isSubmitting.set(true);
    // Resetta eventuali messaggi d'errore ereditati da tentativi di login passati[cite: 1]
    this.errorMessage.set(null);

    // Richiama il servizio di autenticazione passando i dati estratti dal form tramite getRawValue()[cite: 1]
    this.authService.login(this.loginForm.getRawValue()).subscribe({
      // Callback eseguita se il backend accetta le credenziali e restituisce il token JWT[cite: 1]
      next: () => {
        // Disattiva lo stato di caricamento nel Signal[cite: 1]
        this.isSubmitting.set(false);
        // Recupera l'URL da cui l'utente proveniva prima del blocco (es. dalla rotta protetta dell'AuthGuard), altrimenti imposta la Home[cite: 1]
        const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl') ?? '/';
        // Reindirizza l'utente alla pagina di provenienza o alla Home[cite: 1]
        this.router.navigateByUrl(returnUrl);
      },
      // Callback eseguita se il backend rifiuta le credenziali (es. errore 401 Unauthorized o 404)[cite: 1]
      error: () => {
        // Disattiva lo stato di caricamento nel Signal[cite: 1]
        this.isSubmitting.set(false);
        // Valorizza il Signal dell'errore con un messaggio descrittivo da stampare nell'HTML[cite: 1]
        this.errorMessage.set('Email o password non corretti.');
      }
    });
  }
}