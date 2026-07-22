/**
 * GUIDA DIDATTICA DEL FILE
 * ---------------------------------------------------------------------------
 * Prototipo/area impostazioni utente mantenuto per confronto didattico.
 * Leggi prima gli import, poi @Component/@Injectable e infine campi e metodi:
 * questa è la stessa sequenza con cui Angular compone il comportamento del file.
 */
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-user-settings',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user-settings.component.html',
  styleUrl: './user-setting.component.scss'
})
export class UserSettingsComponent {
  
  /** BLOCCO DIDATTICO 1 — FormBuilder iniettato con inject(). */
  private readonly fb = inject(FormBuilder); 

  /** BLOCCO DIDATTICO 2 — Stato simulato della prenotazione tramite signal. */
  readonly prenotazione = signal({
    id: 999,
    camera: 'Camera Doppia Superior',
    checkIn: '2026-07-21',
    checkOut: '2026-07-24',
    prezzoTotale: 390,
    stato: 'PAGATA',
    ospitePrecedente: 'Mario Rossi'
  });

  /** BLOCCO DIDATTICO 3 — Signal che controllano permessi, feedback e loading. */
  readonly haDiritti = signal(true);       
  readonly messaggioInfo = signal<string | null>(null); 
  readonly isSubmitting = signal(false);   

  /** BLOCCO DIDATTICO 4 — Reactive Form con campi obbligatori. */
  readonly updateNameForm = this.fb.group({
    nuovoNome: ['', Validators.required],
    nuovoCognome: ['', Validators.required]
  });

  /** BLOCCO DIDATTICO 5 — Simula l’invio della modifica del nominativo. */
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