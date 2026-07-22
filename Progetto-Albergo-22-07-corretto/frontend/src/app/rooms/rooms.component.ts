import { HttpErrorResponse } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { RouterLink } from '@angular/router';

import { AuthService } from '../core/services/auth.service';
import { BookingService } from '../core/services/booking.service';
import { RoomService } from '../core/services/room.service';

export interface ServizioOpzionale {
  id: number;
  nome: string;
  prezzo: number;
}

export interface RoomCard {
  idCamera: number;
  nome: string;
  prezzoNotte: number;
  serviziFissi: string[];
  descrizione: string;
  capienza: number;
}

@Component({
  selector: 'app-rooms',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterLink],
  templateUrl: './rooms.component.html',
  styleUrl: './rooms.component.scss'
})
export class RoomsComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly roomService = inject(RoomService);
  private readonly bookingService = inject(BookingService);
  readonly authService = inject(AuthService);

  searchForm!: FormGroup;
  readonly isLoading = signal(false);
  readonly isSubmitting = signal(false);
  readonly errorMessage = signal<string | null>(null);

  readonly selectedRoom = signal<RoomCard | null>(null);
  readonly serviziOpzionaliScelti = signal<ServizioOpzionale[]>([]);
  readonly metodoPagamento = signal<'carta' | 'bonifico'>('carta');
  readonly mostraCvc = signal(false);

  readonly prenotazioneConfermata = signal(false);
  readonly codicePrenotazione = signal('');
  readonly emailCliente = signal('');

  readonly allRooms = signal<RoomCard[]>([]);
  readonly filteredRooms = signal<RoomCard[]>([]);

  readonly guestContactForm = this.fb.nonNullable.group({
    nome: ['', [Validators.required, Validators.minLength(2)]],
    cognome: ['', [Validators.required, Validators.minLength(2)]],
    cellulare: ['', [Validators.required, Validators.pattern(/^[0-9+ ()-]{6,20}$/)]],
    email: ['', [Validators.required, Validators.email]]
  });

  // Dati carta usati solo per validazione dell'interfaccia: non vengono inviati al backend né salvati nel DB.
  readonly cardPaymentForm = this.fb.nonNullable.group({
    intestatario: ['', [Validators.required, Validators.minLength(2)]],
    numeroCarta: ['', [Validators.required, Validators.pattern(/^(?:\d[ -]*?){13,19}$/)]],
    scadenza: ['', [Validators.required, Validators.pattern(/^(0[1-9]|1[0-2])\/\d{2}$/)]],
    cvc: ['', [Validators.required, Validators.pattern(/^\d{3,4}$/)]]
  });

  readonly serviziOpzionaliDisponibili: ServizioOpzionale[] = [
    { id: 1, nome: 'Colazione Gourmet in Camera', prezzo: 15 },
    { id: 2, nome: 'Mezza Pensione (Colazione e Cena - al giorno/persona)', prezzo: 35 },
    { id: 3, nome: 'Pensione Completa (Colazione, Pranzo e Cena - al giorno/persona)', prezzo: 60 },
    { id: 4, nome: 'Posto Auto Riservato Garage', prezzo: 20 },
    { id: 5, nome: 'Late Check-out (fino alle 14:00)', prezzo: 35 },
    { id: 6, nome: 'Bottiglia di Champagne di Benvenuto', prezzo: 60 }
  ];


  readonly notti = computed(() => {
    if (!this.searchForm) return 1;
    const checkIn = new Date(this.searchForm.get('checkIn')?.value);
    const checkOut = new Date(this.searchForm.get('checkOut')?.value);
    if (Number.isNaN(checkIn.getTime()) || Number.isNaN(checkOut.getTime()) || checkOut <= checkIn) {
      return 1;
    }
    const diffTime = checkOut.getTime() - checkIn.getTime();
    return Math.max(1, Math.ceil(diffTime / 86400000));
  });

  readonly costoTotale = computed(() => {
    const room = this.selectedRoom();
    if (!room || !this.searchForm) return 0;

    let totale = room.prezzoNotte * this.notti();
    if (this.searchForm.get('pacchettoSpa')?.value) totale += 200;

    const adulti = Number(this.searchForm.get('adulti')?.value) || 1;
    const bambini = Number(this.searchForm.get('bambini')?.value) || 0;
    const totaleOspiti = adulti + bambini;

    const costoServizi = this.serviziOpzionaliScelti().reduce((acc, servizio) => {
      if (servizio.id === 2 || servizio.id === 3) {
        return acc + servizio.prezzo * this.notti() * totaleOspiti;
      }
      return acc + servizio.prezzo;
    }, 0);

    return totale + costoServizi;
  });

  readonly caparra = computed(() => this.costoTotale() * 0.1);
  readonly saldoRimanente = computed(() => this.costoTotale() - this.caparra());

  ngOnInit(): void {
    const today = new Date().toISOString().split('T')[0];
    const tomorrow = new Date(Date.now() + 86400000).toISOString().split('T')[0];

    this.searchForm = this.fb.group({
      checkIn: [today, Validators.required],
      checkOut: [tomorrow, Validators.required],
      adulti: [2, [Validators.required, Validators.min(1)]],
      bambini: [0, [Validators.min(0)]],
      animali: [false],
      pacchettoSpa: [false]
    });

    this.loadRooms();
  }

  onSearch(): void {
    const checkOutControl = this.searchForm.get('checkOut');
    if (checkOutControl?.hasError('dateOrder')) {
      const { dateOrder: _dateOrder, ...remaining } = checkOutControl.errors ?? {};
      checkOutControl.setErrors(Object.keys(remaining).length ? remaining : null);
    }

    if (this.searchForm.invalid) {
      this.searchForm.markAllAsTouched();
      this.errorMessage.set('Controlla i campi evidenziati in rosso.');
      return;
    }

    const checkIn = new Date(this.searchForm.get('checkIn')?.value);
    const checkOut = new Date(this.searchForm.get('checkOut')?.value);
    if (checkOut <= checkIn) {
      checkOutControl?.setErrors({ ...(checkOutControl.errors ?? {}), dateOrder: true });
      checkOutControl?.markAsTouched();
      this.errorMessage.set('La data di check-out deve essere successiva al check-in.');
      return;
    }

    this.errorMessage.set(null);
    this.selectedRoom.set(null);

    const totaleOspiti = this.requestedGuests();
    this.filteredRooms.set(this.allRooms().filter((room) => room.capienza >= totaleOspiti));
  }

  selezionaStanza(room: RoomCard): void {
    this.selectedRoom.set(room);
    this.serviziOpzionaliScelti.set([]);
    this.errorMessage.set(null);
  }

  deselezionaStanza(): void {
    this.selectedRoom.set(null);
    this.errorMessage.set(null);
  }

  toggleServizioOpzionale(servizio: ServizioOpzionale, event: Event): void {
    const checked = (event.target as HTMLInputElement).checked;
    if (checked) {
      this.serviziOpzionaliScelti.update((lista) => [...lista, servizio]);
    } else {
      this.serviziOpzionaliScelti.update((lista) => lista.filter((item) => item.id !== servizio.id));
    }
  }

  toggleMostraCvc(): void {
    this.mostraCvc.update((value) => !value);
  }

  confermaEPay(): void {
    const room = this.selectedRoom();
    if (!room) return;

    const isCustomer = this.authService.isCustomer();
    if (!isCustomer && this.guestContactForm.invalid) {
      this.guestContactForm.markAllAsTouched();
      this.errorMessage.set('Controlla i campi evidenziati in rosso prima di confermare la prenotazione.');
      return;
    }

    if (this.metodoPagamento() === 'carta' && this.cardPaymentForm.invalid) {
      this.cardPaymentForm.markAllAsTouched();
      this.errorMessage.set('Controlla i dati della carta evidenziati in rosso.');
      return;
    }

    const checkIn = String(this.searchForm.get('checkIn')?.value ?? '');
    const checkOut = String(this.searchForm.get('checkOut')?.value ?? '');

    this.isSubmitting.set(true);
    this.errorMessage.set(null);

    const request$ = isCustomer
      ? this.bookingService.createBooking({
          roomId: room.idCamera,
          checkIn,
          checkOut,
          numeroOspiti: this.requestedGuests()
        })
      : this.bookingService.createGuestBooking({
          roomId: room.idCamera,
          checkIn,
          checkOut,
          numeroOspiti: this.requestedGuests(),
          ospite: this.guestContactForm.getRawValue()
        });

    request$.subscribe({
      next: (booking) => {
        this.isSubmitting.set(false);
        this.codicePrenotazione.set(`PREN-${booking.id}`);
        this.emailCliente.set(
          this.authService.currentUser()?.email ?? this.guestContactForm.controls.email.value
        );
        this.prenotazioneConfermata.set(true);
      },
      error: (error: HttpErrorResponse) => {
        this.isSubmitting.set(false);
        if (error.status === 409) {
          this.errorMessage.set('Questa camera non è più disponibile nelle date selezionate.');
        } else if (error.status === 400) {
          if (!isCustomer) {
            this.guestContactForm.markAllAsTouched();
            this.applyServerFieldErrors(error);
          }
          this.errorMessage.set('Controlla i campi evidenziati in rosso e riprova.');
        } else if (error.status === 401 || error.status === 403) {
          this.errorMessage.set('La sessione non è valida. Esci oppure prenota senza account.');
        } else {
          this.errorMessage.set('Non è stato possibile salvare la prenotazione nel database.');
        }
      }
    });
  }

  nuovaPrenotazione(): void {
    this.prenotazioneConfermata.set(false);
    this.selectedRoom.set(null);
    this.serviziOpzionaliScelti.set([]);
    this.errorMessage.set(null);
    this.guestContactForm.reset();
    this.cardPaymentForm.reset();
  }

  onImgError(event: Event): void {
    (event.target as HTMLImageElement).src = 'camere/camera-1.jpg';
  }

  private loadRooms(): void {
    this.isLoading.set(true);
    this.roomService.getRooms().subscribe({
      next: (rooms) => {
        const mapped = rooms.map((room) => this.toRoomCard(room));
        const finalRooms = mapped.length > 0 ? mapped : this.generateFallbackRooms();
        this.allRooms.set(finalRooms);
        this.filteredRooms.set(finalRooms.filter((room) => room.capienza >= this.requestedGuests()));
        this.isLoading.set(false);
      },
      error: () => {
        const fallback = this.generateFallbackRooms();
        this.allRooms.set(fallback);
        this.filteredRooms.set(fallback.filter((room) => room.capienza >= this.requestedGuests()));
        this.isLoading.set(false);
      }
    });
  }

  private toRoomCard(room: {
    idCamera: number;
    nome?: string;
    descrizione?: string;
    prezzoPerNotte?: number;
    occupanti?: number;
  }): RoomCard {
    const id = room.idCamera;
    const tipi = ['Standard', 'Deluxe', 'Junior Suite', 'Suite Panorama', 'Executive Suite'];
    const tipo = tipi[id % tipi.length];
    const capienza = room.occupanti && room.occupanti > 0 ? room.occupanti : (id % 4) + 2;

    return {
      idCamera: id,
      nome: room.nome?.trim() || `${tipo} Camera ${id}`,
      prezzoNotte: room.prezzoPerNotte ?? 80 + (id % 5) * 25,
      capienza,
      serviziFissi: ['Wi-Fi 6', 'Aria Condizionata', 'Smart TV', 'Minibar'],
      descrizione:
        room.descrizione?.trim() ||
        `Elegante ${tipo.toLowerCase()} per massimo ${capienza} ospiti con tutti i comfort moderni.`
    };
  }

  private generateFallbackRooms(): RoomCard[] {
    return Array.from({ length: 50 }, (_, index) => {
      const id = index + 1;
      return this.toRoomCard({ idCamera: id });
    });
  }

  requestedGuests(): number {
    const adulti = Number(this.searchForm?.get('adulti')?.value) || 1;
    const bambini = Number(this.searchForm?.get('bambini')?.value) || 0;
    return Math.max(1, Math.min(8, adulti + bambini));
  }

  isInvalid(control: AbstractControl | null): boolean {
    return !!control && control.invalid && (control.touched || control.dirty);
  }

  private applyServerFieldErrors(error: HttpErrorResponse): void {
    const message = String(error.error?.message ?? error.error?.detail ?? error.message ?? '').toLowerCase();
    const fields: Array<keyof typeof this.guestContactForm.controls> = ['nome', 'cognome', 'cellulare', 'email'];

    for (const field of fields) {
      if (!message.includes(field)) continue;
      const control = this.guestContactForm.controls[field];
      control.setErrors({ ...(control.errors ?? {}), server: true });
    }
  }
}
