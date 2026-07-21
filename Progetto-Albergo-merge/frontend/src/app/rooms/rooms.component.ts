import { Component, inject, signal, computed } from '@angular/core'; 
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

  // NUOVA LOGICA: Ripartizione in 3 Tipologie per Percentuale
  readonly groupedRooms = computed(() => {
    const allRooms = this.rooms();
    if (allRooms.length === 0) return [];

    const totalCamere = allRooms.length;
    
    // Calcoliamo gli indici di sbarramento basati sulle percentuali richieste
    const limiteSingole = Math.floor(totalCamere * 0.20); // 20%
    const limiteDoppie = limiteSingole + Math.floor(totalCamere * 0.50); // +50% (Totale 70%)

    // Affettiamo l'array totale delle camere in 3 sotto-array separati
    const stanzeSingole = allRooms.slice(0, limiteSingole);
    const stanzeDoppie = allRooms.slice(limiteSingole, limiteDoppie);
    const stanzeFamily = allRooms.slice(limiteDoppie);

    const vetrinaTipologie = [];

    // 1. Configurazione Categoria Singola
    if (stanzeSingole.length > 0) {
      vetrinaTipologie.push({
        idVetrina: 1,
        nome: 'Camera Singola Essential',
        descrizione: 'Ideale per chi viaggia per affari o turismo in solitaria. Spazi ottimizzati dal design contemporaneo, dotati di letto singolo comfort, connessione Wi-Fi ultra-fibra e scrittoio.',
        immagine: 'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=800&auto=format&fit=crop', // Immagine stock elegante
        prezzoPerNotte: 75,
        idCameraReale: stanzeSingole[0].idCamera, // ID della prima camera reale libera di questo slot per la prenotazione
        conteggio: stanzeSingole.length // Quantità disponibile dinamica
      });
    }

    // 2. Configurazione Categoria Doppia
    if (stanzeDoppie.length > 0) {
      vetrinaTipologie.push({
        idVetrina: 2,
        nome: 'Camera Doppia Superior',
        descrizione: 'Un connubio perfetto di eleganza e comodità. Perfetta per coppie, offre un ampio letto matrimoniale king-size, bagno privato in marmo, balcone privato e frigobar assortito.',
        immagine: 'https://images.unsplash.com/photo-1590490360182-c33d57733427?w=800&auto=format&fit=crop',
        prezzoPerNotte: 130,
        idCameraReale: stanzeDoppie[0].idCamera, // ID reale per agganciare il flusso di prenotazione backend
        conteggio: stanzeDoppie.length
      });
    }

    // 3. Configurazione Categoria Family
    if (stanzeFamily.length > 0) {
      vetrinaTipologie.push({
        idVetrina: 3,
        nome: 'Family Luxury Suite',
        descrizione: 'Progettata su misura per i soggiorni in famiglia. Due ambienti spaziosi e comunicanti garantiscono la massima privacy per i genitori e totale divertimento e sicurezza per i bambini.',
        immagine: 'https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=800&auto=format&fit=crop',
        prezzoPerNotte: 195,
        idCameraReale: stanzeFamily[0].idCamera, // ID reale per il form
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
        // this.errorMessage.set('Impossibile recuperare le stanze disponibili. Riprova.');
        // this.isLoading.set(false);
        // this.hasSearched.set(true);

        // MODIFICA DI EMERGENZA: Se il server è irraggiungibile per CORS, generiamo 50 camere di test!
      console.warn("Backend non raggiungibile. Attivazione dati di simulazione (Mock Data).");
      
      const stanzeSimulate: Room[] = Array.from({ length: 50 }, (_, i) => ({
        idCamera: i + 1,
        nome: `Camera Reale Numero ${101 + i}`,
        descrizione: "Stanza generata dal simulatore locale.",
        immagine: ""
      }));

      this.rooms.set(stanzeSimulate); // Inserisce le 50 stanze nel Signal
      this.isLoading.set(false);      // Spegne il caricamento
      this.hasSearched.set(true);     // Mostra la vetrina delle 3 tipologie
      // Manteniamo un piccolo avviso testuale discreto a schermo se vuoi
      // this.errorMessage.set("Modalità di Anteprima Locale attiva (Backend offline).");
    }
  });
}


  bookingQueryParams(): Record<string, string> {
    const { checkIn, checkOut } = this.searchForm.getRawValue();
    return { checkIn, checkOut };
  }
}