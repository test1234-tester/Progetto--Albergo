/**
 * GUIDA DIDATTICA DEL FILE
 * ---------------------------------------------------------------------------
 * Componente riutilizzabile che presenta i dati sintetici di una camera.
 * È un buon esempio di componente "presentational": riceve dati dal padre con
 * @Input e non possiede logica di accesso al backend.
 */
import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Room } from '../../core/models/room.model';

@Component({
  selector: 'app-room-card',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './room-card.component.html',
  styleUrl: './room-card.component.scss'
})
export class RoomCardComponent {
  /** BLOCCO DIDATTICO 1 — Dati obbligatori della camera da visualizzare. */
  @Input({ required: true }) room!: Room;

  /**
   * BLOCCO DIDATTICO 2 — Parametri opzionali da mantenere quando l'utente
   * apre la route di prenotazione (per esempio check-in e check-out).
   */
  @Input() bookingQueryParams: Record<string, string> = {};

  /** BLOCCO DIDATTICO 3 — Numero di camere disponibili mostrato nel badge. */
  @Input() disponibiliCount?: number;
}
