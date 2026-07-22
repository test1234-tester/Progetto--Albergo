import { Component, computed, inject, signal } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators
} from '@angular/forms';
import { RouterLink } from '@angular/router';

import { Room } from '../core/models/room.model';
import { RoomService } from '../core/services/room.service';

function dateRangeValidator(): ValidatorFn {
  return (group: AbstractControl): ValidationErrors | null => {
    const checkIn = group.get('checkIn')?.value;
    const checkOut = group.get('checkOut')?.value;
    if (!checkIn || !checkOut) {
      return null;
    }
    return checkOut > checkIn ? null : { invalidRange: true };
  };
}

@Component({
  selector: 'app-rooms',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './rooms.component.html',
  styleUrl: './rooms.component.scss'
})
export class RoomsComponent {
  private readonly fb = inject(FormBuilder);
  private readonly roomService = inject(RoomService);

  readonly searchForm = this.fb.nonNullable.group(
    {
      checkIn: ['', Validators.required],
      checkOut: ['', Validators.required]
    },
    { validators: dateRangeValidator() }
  );

  readonly rooms = signal<Room[]>([]);
  readonly isLoading = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly hasSearched = signal(false);

  // Ripartizione grafica delle camere disponibili in tre tipologie.
  readonly groupedRooms = computed(() => {
    const allRooms = this.rooms();
    if (allRooms.length === 0) return [];

    const totalCamere = allRooms.length;
    const limiteSingole = Math.floor(totalCamere * 0.20);
    const limiteDoppie = limiteSingole + Math.floor(totalCamere * 0.50);

    const stanzeSingole = allRooms.slice(0, limiteSingole);
    const stanzeDoppie = allRooms.slice(limiteSingole, limiteDoppie);
    const stanzeFamily = allRooms.slice(limiteDoppie);

    const vetrinaTipologie = [];

    if (stanzeSingole.length > 0) {
      vetrinaTipologie.push({
        idVetrina: 1,
        nome: 'Camera Singola Essential',
        descrizione:
          'Ideale per chi viaggia per affari o turismo in solitaria. Spazi ottimizzati dal design contemporaneo, dotati di letto singolo comfort, connessione Wi-Fi ultra-fibra e scrittoio.',
        immagine:
          'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=800&auto=format&fit=crop',
        prezzoPerNotte: 75,
        idCameraReale: stanzeSingole[0].idCamera,
        conteggio: stanzeSingole.length
      });
    }

    if (stanzeDoppie.length > 0) {
      vetrinaTipologie.push({
        idVetrina: 2,
        nome: 'Camera Doppia Superior',
        descrizione:
          'Un connubio perfetto di eleganza e comodità. Perfetta per coppie, offre un ampio letto matrimoniale king-size, bagno privato in marmo, balcone privato e frigobar assortito.',
        immagine:
          'https://images.unsplash.com/photo-1590490360182-c33d57733427?w=800&auto=format&fit=crop',
        prezzoPerNotte: 130,
        idCameraReale: stanzeDoppie[0].idCamera,
        conteggio: stanzeDoppie.length
      });
    }

    if (stanzeFamily.length > 0) {
      vetrinaTipologie.push({
        idVetrina: 3,
        nome: 'Family Luxury Suite',
        descrizione:
          'Progettata su misura per i soggiorni in famiglia. Due ambienti spaziosi e comunicanti garantiscono la massima privacy per i genitori e totale divertimento e sicurezza per i bambini.',
        immagine:
          'https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=800&auto=format&fit=crop',
        prezzoPerNotte: 195,
        idCameraReale: stanzeFamily[0].idCamera,
        conteggio: stanzeFamily.length
      });
    }

    return vetrinaTipologie;
  });

  onSearch(): void {
    if (this.searchForm.invalid) {
      this.searchForm.markAllAsTouched();
      return;
    }

    const { checkIn, checkOut } = this.searchForm.getRawValue();
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.roomService.getRooms(checkIn, checkOut).subscribe({
      next: (rooms) => {
        this.rooms.set(rooms);
        this.isLoading.set(false);
        this.hasSearched.set(true);
      },
      error: () => {
        // Mantiene il fallback aggiunto dal gruppo per permettere la demo anche a backend offline.
        console.warn('Backend non raggiungibile. Attivazione dati di simulazione (Mock Data).');

        const stanzeSimulate: Room[] = Array.from({ length: 50 }, (_, i) => ({
          idCamera: i + 1,
          nome: `Camera Reale Numero ${101 + i}`,
          descrizione: 'Stanza generata dal simulatore locale.',
          immagine: ''
        }));

        this.rooms.set(stanzeSimulate);
        this.isLoading.set(false);
        this.hasSearched.set(true);
      }
    });
  }

  bookingQueryParams(): Record<string, string> {
    const { checkIn, checkOut } = this.searchForm.getRawValue();
    return { checkIn, checkOut };
  }
}
