// Questo component aggiunge una validazione personalizzata per verificare che la password e la conferma coincidano, oltre a ripulire i dati prima di inviarli al backend.

// Importa le funzionalità nucleo di Angular per creare il componente, iniettare dipendenze e gestire i Signals
import { Component, inject, signal } from '@angular/core';
// Importa gli strumenti avanzati dei Reactive Forms per costruire il form e convalidare i campi
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators
} from '@angular/forms';
// Importa il router per gestire i reindirizzamenti tra le pagine dell'applicazione[cite: 1]
import { Router, RouterLink } from '@angular/router';
// Importa il servizio AuthService per comunicare con l'endpoint di registrazione del server[cite: 1]
import { AuthService } from '../../core/services/auth.service';
import { RegisterRequest } from '../../core/models/auth.model'; // Importiamo il tipo per la validazione


// Validatore a livello di gruppo: password e conferma devono coincidere
function passwordsMatchValidator(): ValidatorFn {
  return (group: AbstractControl): ValidationErrors | null => {
    // Estrae il valore inserito nel primo campo password
    const password = group.get('password')?.value;
    // Estrae il valore inserito nel campo di conferma password
    const confirmPassword = group.get('confirmPassword')?.value;
    // Se coincidono restituisce null (valido), altrimenti l'errore personalizzato 'passwordsMismatch'
    return password === confirmPassword ? null : { passwordsMismatch: true };
  };
}

// Validatore personalizzato: se il ruolo è ADMIN, il codice segreto diventa obbligatorio
function adminCodeValidator(): ValidatorFn {
  return (group: AbstractControl): ValidationErrors | null => {
    const ruolo = group.get('ruolo')?.value;
    const codiceAdmin = group.get('codiceAdmin')?.value;

// Se seleziona ADMIN ma il codice è vuoto, restituisce l'errore
    if (ruolo === 'ADMIN' && !codiceAdmin) {
      return { adminCodeRequired: true };
    }
    return null;
  };
}    

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  // Inietta lo strumento FormBuilder per creare il gruppo di controlli in modo rapido
  private readonly fb = inject(FormBuilder);
  // Inietta il servizio di autenticazione per inviare i dati al database tramite API
  private readonly authService = inject(AuthService);
  // Inietta il router per reindirizzare l'utente dopo il successo della registrazione
  private readonly router = inject(Router);

  // STATI REATTIVI MESSI IN CIMA: Evita qualsiasi problema di cascata di errori di compilazione
  readonly isSubmitting = signal(false);
  readonly errorMessage = signal<string | null>(null);

  // Inizializza il form reattivo mappando i campi 1:1 con le colonne del Database MySQL
readonly registerForm = this.fb.nonNullable.group(
    {
      nome: ['', Validators.required],
      cognome: ['', Validators.required],
      username: ['', Validators.required],
      cellulare: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required],
      ruolo: ['CLIENTE', Validators.required],
      codiceAdmin: ['']
    },
    { validators: [passwordsMatchValidator(), adminCodeValidator()] }
  ); // Applica entrambi i validatori al gruppo  );

  // Signal booleano per tracciare lo stato della richiesta HTTP e bloccare temporaneamente la UI
  // readonly isSubmitting = signal(false);
  // Signal testuale per ospitare messaggi di errore restituiti dal server in caso di fallimento
  // readonly errorMessage = signal<string | null>(null);

  // Metodo attivato all'invio del form
  onSubmit(): void {
    // Se il form non rispetta tutte le regole di validazione, blocca l'invio
    if (this.registerForm.invalid) { // Mostra visivamente tutti gli errori grafici nell'HTML
      this.registerForm.markAllAsTouched();
      return;
    }

    // Cambia il Signal a true per mostrare scritte di caricamento o disabilitare bottoni
    this.isSubmitting.set(true);
    // Ripulisce eventuali messaggi d'errore di tentativi falliti in precedenza
    this.errorMessage.set(null);

    // Estraggom i dati dal form escludendo confirmPassword e codiceAdmin prima dell'invio
    const { confirmPassword, codiceAdmin, ...payload } = this.registerForm.getRawValue();

    // // Nota di sicurezza: Se il ruolo è ADMIN, puoi fare un controllo locale sul codice prima di inviare, 
    // // ad esempio verificare che sia uguale a "HOTEL2026STAFF"
    // if (this.registerForm.getRawValue().ruolo === 'ADMIN' && codiceAdmin !== 'HOTEL2026') {
    //   this.isSubmitting.set(false);
    //   this.errorMessage.set('Codice Passkey Admin non valido. Impossibile creare un utente Staff.');
    //   return;
    // }

    // // Invia il payload (che ora include il campo "ruolo") al backend[cite: 1]
    // this.authService.register(payload).subscribe({
    //   next: () => {
    //     this.isSubmitting.set(false);
    //     this.router.navigate(['/']);
    //   },
    //   error: () => {
    //     this.isSubmitting.set(false);
    //     this.errorMessage.set('Registrazione non riuscita. Controlla i dati inseriti o l\'account già esistente.');
    //   }
    // });
//       }
// }

 
    // 1. Controllo di sicurezza sul codice Passkey se l'utente sceglie ADMIN
    if (payload.ruolo === 'ADMIN' && codiceAdmin !== 'HOTEL2026') {
      this.isSubmitting.set(false);
      this.errorMessage.set('Codice Passkey Admin non valido. Impossibile creare un utente Staff.');
      return;
    }

    // 2. 🔥 IL CAMBIAMENTO: Adattiamo il tipo del ruolo da stringa generica a stringa specifica
    const registerPayload: RegisterRequest = {
      ...payload,
      ruolo: payload.ruolo as 'CLIENTE' | 'ADMIN'
    };

    // 3. Invio del pacchetto dati perfettamente tipizzato al backend
    this.authService.register(registerPayload).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.router.navigate(['/']);
      },
      error: () => {
        this.isSubmitting.set(false);
        this.errorMessage.set('Registrazione non riuscita. Controlla i dati o la connessione.');
      }
    });
  }
}
 
