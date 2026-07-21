import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { HttpErrorResponse } from '@angular/common/http';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { Room } from '../../core/models/room.model';
import { BookingService } from '../../core/services/booking.service';
import { RoomService } from '../../core/services/room.service';
import { SpaService } from '../../core/services/spa.service';
import { PricingService } from '../../core/services/pricing.service';

@Component({
  selector: 'app-booking-form',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './booking-form.component.html',
  styleUrl: './booking-form.component.scss'
})
export class BookingFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly roomService = inject(RoomService);
  private readonly bookingService = inject(BookingService);
  private readonly spaService = inject(SpaService);
  private readonly pricingService = inject(PricingService);

  private readonly roomId = Number(this.route.snapshot.paramMap.get('id'));
  // Date scelte nella pagina /stanze, arrivano come queryParams
  readonly checkIn = this.route.snapshot.queryParamMap.get('checkIn') ?? '';
  readonly checkOut = this.route.snapshot.queryParamMap.get('checkOut') ?? '';
  readonly nights = this.pricingService.nightsBetween(this.checkIn, this.checkOut);

  readonly room = signal<Room | null>(null);
  readonly isLoadingRoom = signal(true);
  readonly isSubmitting = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly missingDates = !this.checkIn || !this.checkOut;

  readonly bookingForm = this.fb.group({
    trattamento: ['', Validators.required],
    spaAbbinata: [false],
    guests: this.fb.array([this.createGuestGroup()])
  });

  get guests(): FormArray {
    return this.bookingForm.get('guests') as FormArray;
  }

  // Segnale derivato dal toggle "Aggiungi Spa": aggiorna il prezzo totale in tempo reale nel form
  private readonly spaAbbinata = toSignal(this.bookingForm.controls.spaAbbinata.valueChanges, {
    initialValue: false
  });

  readonly totalAmount = computed(() =>
    this.pricingService.calculateStayAmount(
      this.room()?.prezzoPerNotte ?? 0,
      this.nights,
      this.spaAbbinata() ?? false
    )
  );

  // ngOnInit(): void {
  //   this.roomService.getRoomById(this.roomId).subscribe({
  //     next: (room) => {
  //       this.room.set(room);
  //       this.isLoadingRoom.set(false);
  //     },
  //     error: () => {
  //       this.errorMessage.set('Stanza non trovata.');
  //       this.isLoadingRoom.set(false);
  //     }
  //   });
  // }

  //versione modificata per visualizzazione senza be
  ngOnInit(): void {
  this.roomService.getRoomById(this.roomId).subscribe({
    next: (room) => {
      this.room.set(room);
      this.isLoadingRoom.set(false);
    },
    error: () => {
      // 🚨 MODIFICA DI EMERGENZA: Simuliamo la stanza visto che il backend è offline
      console.warn("Backend offline. Creazione di una stanza finta per il form.");
      
      const stanzaSimulata: Room = {
        idCamera: this.roomId,
        nome: `Camera Simulata Premium (N. ${this.roomId})`,
        descrizione: "Stanza di test caricata in modalità anteprima locale.",
        immagine: "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=800&auto=format&fit=crop",
        prezzoPerNotte: 130 // Prezzo di prova fondamentale per attivare i calcoli del totale!
      };

      this.room.set(stanzaSimulata);  // Salva la stanza simulata nel Signal
      this.isLoadingRoom.set(false); // Spegne la scritta "Caricamento..."
      this.errorMessage.set(null);   // Resetta l'errore per non bloccare la pagina
    }
  });
}

  private createGuestGroup(): FormGroup {
    return this.fb.group({
      nome: ['', Validators.required],
      cognome: ['', Validators.required]
    });
  }

  addGuest(): void {
    this.guests.push(this.createGuestGroup());
  }

  removeGuest(index: number): void {
    // Almeno un ospite deve restare sempre presente (chi arriva deve essere dichiarato)
    if (this.guests.length > 1) {
      this.guests.removeAt(index);
    }
  }

  
  ////versione vecchia
  // onSubmit(): void {
  //   if (this.bookingForm.invalid || this.missingDates) {
  //     this.bookingForm.markAllAsTouched();
  //     if (this.missingDates) {
  //       this.errorMessage.set('Date mancanti: torna alla ricerca stanze e riprova.');
  //     }
  //     return;
  //   }

  //   this.isSubmitting.set(true);
  //   this.errorMessage.set(null);

  //   this.bookingService
  //     .createBooking({
  //       roomId: this.roomId,
  //       checkIn: this.checkIn,
  //       checkOut: this.checkOut,
  //       guests: this.bookingForm.getRawValue().guests as { nome: string; cognome: string }[]
  //       // trattamento viaggia con la prenotazione: se il contratto di BookingRequest lato backend
  //       // non lo prevede ancora, va aggiunto lì insieme al campo trattamento qui sopra
  //     })
  //     .subscribe({
  //       next: (booking) => {
  //         if (this.bookingForm.getRawValue().spaAbbinata) {
  //           this.spaService.addSpaToStay(booking.id).subscribe();
  //         }
  //         this.isSubmitting.set(false);
  //         this.router.navigate(['/prenotazioni', booking.id, 'pagamento'], {
  //           queryParams: { totalAmount: this.totalAmount() }
  //         });
  //       },
  //       error: (err: HttpErrorResponse) => {
  //         this.isSubmitting.set(false);
  //         if (err.status === 409) {
  //           // Vincolo anti-duplicati lato backend: la stanza non è più libera per queste date
  //           this.errorMessage.set('Questa stanza non è più disponibile per le date scelte.');
  //         } else {
  //           this.errorMessage.set('Non è stato possibile completare la prenotazione. Riprova.');
  //         }
  //       }
  //     });
  // }


  //Prova termporanea per simulare la risposta ed evitare il cors
onSubmit(): void {
  if (this.bookingForm.invalid || this.missingDates) {
    this.bookingForm.markAllAsTouched();
   if (this.missingDates) {
      this.errorMessage.set('Date mancanti: torna alla ricerca stanze e riprova.');
    }
    return;
  }

  this.isSubmitting.set(true);
  this.errorMessage.set(null);

  // =========================================================================
  // 1. VERSIONE TEMPORANEA (SIMULAZIONE LOCALE FRONTEND - ATTIVA)
  // =========================================================================
  console.warn("[MOCK] Invio prenotazione simulato in corso (Backend offline)...");

  // SIMULAZIONE LOCALE DI EMERGENZA
  setTimeout(() => {
    this.isSubmitting.set(false);
    // Generiamo un ID prenotazione finto (es: 999) per superare lo step
    const bookingSimulato = { id: 999 }; 
    
    console.log("[MOCK] Prenotazione creata con successo! Vado al pagamento.");

    // Facciamo finta che il server ci abbia risposto creandoci la prenotazione ID: 999
// NOTA DI EMERGENZA: Se la pagina di pagamento ti rimbalza ancora in Home, 
    // controlla se nel file 'app.routes.ts' la rotta ha un 'canActivate: [authGuard]'.
    // Se sì, assicurati di aver fatto prima il Login nell'app!
    this.router.navigate(['/prenotazioni', bookingSimulato.id, 'pagamento'], {
      queryParams: { totalAmount: this.totalAmount() }
    });
  }, 1000); // Rende visibile l'effetto "Invio in corso..." per 1 secondo
// / =========================================================================
  // 2. VERSIONE CORRETTA (COMUNICAZIONE BACKEND REALE - COMMENTATA)
  // =========================================================================
  /*
  // Costruiamo il payload definitivo includendo il campo 'trattamento' che prima era stato dimenticato!
  const payload = {
    roomId: this.roomId,
    checkIn: this.checkIn,
    checkOut: this.checkOut,
    guests: this.bookingForm.getRawValue().guests as { nome: string; cognome: string }[],
    trattamento: this.bookingForm.getRawValue().trattamento // <-- Sistemato qui per il backend
  };

  this.bookingService
    .createBooking(payload)
    .subscribe({
      next: (booking) => {
        // Se l'utente ha spuntato la Spa, invia la richiesta aggiuntiva
        if (this.bookingForm.getRawValue().spaAbbinata) {
          this.spaService.addSpaToStay(booking.id).subscribe();
        }
        this.isSubmitting.set(false);
        this.router.navigate(['/prenotazioni', booking.id, 'pagamento'], {
          queryParams: { totalAmount: this.totalAmount() }
        });
      },
      error: (err: HttpErrorResponse) => {
        this.isSubmitting.set(false);
        if (err.status === 409) {
          this.errorMessage.set('Questa stanza non è più disponibile per le date scelte.');
        } else {
          this.errorMessage.set('Non è stato possibile completare la prenotazione. Riprova.');
        }
      }
    });
  */
}
}
