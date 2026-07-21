// Importa il decoratore Component e l'Input per accettare dati provenienti dal componente padre
import { Component, Input } from '@angular/core';
// Importa il modulo delle rotte per permettere all'HTML di effettuare reindirizzamenti interni senza rinfrescare il browser[cite: 1]
import { RouterLink } from '@angular/router';
// Importa l'interfaccia strutturale della stanza per la sicurezza dei tipi[cite: 1]
import { Room } from '../../core/models/room.model';

@Component({
  selector: 'app-room-card',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './room-card.component.html', // Ora si trova nella cartella corretta!
  styleUrl: './room-card.component.scss'
})
export class RoomCardComponent {
  // Proprietà di input obbligatoria: riceve i dati della stanza da mostrare nella card[cite: 1]
  @Input({ required: true }) room!: Room;
  // Proprietà di input opzionale: riceve le date selezionate dall'utente sotto forma di chiave-valore[cite: 1]
  @Input() bookingQueryParams: Record<string, string> = {};
  // NUOVO INPUT: Riceve il numero totale di camere disponibili per questa specifica tipologia[cite: 1]
  @Input() disponibiliCount?: number; // <-- Nuova proprietà input per il badge contatore
}