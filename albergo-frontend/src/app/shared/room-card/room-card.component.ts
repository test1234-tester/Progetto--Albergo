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
  @Input({ required: true }) room!: Room;
  // Date di ricerca da propagare al form di prenotazione (query params sulla rotta /prenota)
  @Input() bookingQueryParams: Record<string, string> = {};
}
