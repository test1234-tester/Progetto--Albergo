import { Component, signal, inject } from '@angular/core'; // 💡 Importato correttamente 'inject'
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-user-settings',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule], 
  templateUrl: './user-settings.component.html',
  styles: [] // 💡 Rimosso lo stylesheet esterno per evitare l'errore del file .scss mancante
})
export class UserSettingsComponent {
  
  // 1. Inizializziamo il FormBuilder usando la funzione inject di Angular
  private readonly fb = inject(FormBuilder); 

  // 2. STATO DELLA PRENOTAZIONE (SIMULATO VIA SIGNAL)
  readonly prenotazione = signal({
    id: 999,
    camera: 'Camera Doppia Superior',
    checkIn: '2026-07-21',
    checkOut: '2026-07-24',
    prezzoTotale: 390,
    stato: 'PAGATA',
    ospitePrecedente: 'Mario Rossi'
  });

  // 3. STATI DI CONTROLLO DELL'INTERFACCIA
  readonly haDiritti = signal(true);       
  readonly messaggioInfo = signal<string | null>(null); 
  readonly isSubmitting = signal(false);   

  // 4. VALIDAZIONE DEL FORM (Dichiarato una volta sola)
  readonly updateNameForm = this.fb.group({
    nuovoNome: ['', Validators.required],
    nuovoCognome: ['', Validators.required]
  });

  // 5. FUNZIONE DI INVIO DELLE MODIFICHE
  onUpdateNominativo(): void {
    if (this.updateNameForm.invalid || !this.haDiritti()) return;

    const { nuovoNome, nuovoCognome } = this.updateNameForm.getRawValue();
    const nuovoNominativoCompleto = `${nuovoNome} ${nuovoCognome}`;

    this.isSubmitting.set(true);

    // =========================================================================
    // VERSIONE TEMPORANEA (SIMULAZIONE FRONTEND ATTIVA)
    // =========================================================================
    setTimeout(() => {
      this.isSubmitting.set(false);

      if (nuovoNominativoCompleto.toLowerCase() !== this.prenotazione().ospitePrecedente.toLowerCase()) {
        this.haDiritti.set(false); // L'utente perde i diritti di modifica nel frontend
        this.messaggioInfo.set(
          `Prenotazione ceduta con successo a ${nuovoNominativoCompleto}. Non disponi più dei diritti di modifica su questa prenotazione.`
        );
      } else {
        this.messaggioInfo.set("Nominativo aggiornato con successo (proprietario invariato).");
      }
    }, 1200);

    // =========================================================================
    // VERSIONE REALE PER IL BACKEND (COMMENTATA)
    // =========================================================================
    /*
    const payload = { nuovoNominativo: nuovoNominativoCompleto };
    
    this.http.patch(`/api/prenotazioni/${this.prenotazione().id}/cambio-nominativo`, payload)
      .subscribe({
        next: (rispostaDB) => {
          this.isSubmitting.set(false);
          this.haDiritti.set(rispostaDB.haDiritti);
          this.messaggioInfo.set(rispostaDB.message);
        },
        error: () => {
          this.isSubmitting.set(false);
          this.messaggioInfo.set("Errore critico durante la comunicazione con il server.");
        }
      });
    */
  }
}