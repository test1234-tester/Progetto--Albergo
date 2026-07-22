import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { Room } from '../../core/models/room.model';
import { AuthService } from '../../core/services/auth.service';
import { BookingService } from '../../core/services/booking.service';
import { PricingService } from '../../core/services/pricing.service';
import { RoomService } from '../../core/services/room.service';

@Component({
  selector: 'app-booking-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './booking-form.component.html',
  styleUrl: './booking-form.component.scss'
})
export class BookingFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly roomService = inject(RoomService);
  private readonly bookingService = inject(BookingService);
  private readonly pricingService = inject(PricingService);
  private readonly authService = inject(AuthService);

  private readonly roomId = Number(this.route.snapshot.paramMap.get('id'));
  readonly checkIn = this.route.snapshot.queryParamMap.get('checkIn') ?? '';
  readonly checkOut = this.route.snapshot.queryParamMap.get('checkOut') ?? '';
  readonly requestedAdults = Math.max(
    1,
    Number(this.route.snapshot.queryParamMap.get('adulti') ?? 1) || 1
  );
  readonly requestedChildren = Math.max(
    0,
    Number(this.route.snapshot.queryParamMap.get('bambini') ?? 0) || 0
  );
  readonly requestedGuestCount = Math.min(8, this.requestedAdults + this.requestedChildren);
  readonly nights = this.pricingService.nightsBetween(this.checkIn, this.checkOut);
  readonly isCustomer = this.authService.isCustomer;

  readonly room = signal<Room | null>(null);
  readonly isLoadingRoom = signal(true);
  readonly isSubmitting = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly missingDates = !this.checkIn || !this.checkOut;

  readonly bookingForm = this.fb.group({
    trattamento: ['COLAZIONE', Validators.required],
    spaAbbinata: [false],
    guests: this.fb.array([this.createGuestGroup()])
  });

  readonly anonymousForm = this.fb.nonNullable.group({
    nome: ['', Validators.required],
    cognome: ['', Validators.required],
    cellulare: ['', [Validators.required, Validators.minLength(6)]],
    email: ['', [Validators.required, Validators.email]]
  });

  get guests(): FormArray {
    return this.bookingForm.get('guests') as FormArray;
  }

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

  ngOnInit(): void {
    for (let index = 1; index < this.requestedGuestCount; index += 1) {
      this.guests.push(this.createGuestGroup());
    }

    this.roomService.getRoomById(this.roomId).subscribe({
      next: (room) => {
        this.room.set(room);
        this.isLoadingRoom.set(false);
      },
      error: () => {
        this.errorMessage.set('Camera non trovata o backend non raggiungibile.');
        this.isLoadingRoom.set(false);
      }
    });
  }

  addGuest(): void {
    if (this.guests.length < 8) {
      this.guests.push(this.createGuestGroup());
    }
  }

  removeGuest(index: number): void {
    if (this.guests.length > 1) {
      this.guests.removeAt(index);
    }
  }

  onSubmit(): void {
    const anonymousInvalid = !this.isCustomer() && this.anonymousForm.invalid;
    if (this.bookingForm.invalid || anonymousInvalid || this.missingDates) {
      this.bookingForm.markAllAsTouched();
      if (!this.isCustomer()) this.anonymousForm.markAllAsTouched();
      if (this.missingDates) {
        this.errorMessage.set('Date mancanti: torna alla ricerca camere e riprova.');
      }
      return;
    }

    const guests = this.bookingForm.getRawValue().guests as { nome: string; cognome: string }[];
    const basePayload = {
      roomId: this.roomId,
      checkIn: this.checkIn,
      checkOut: this.checkOut,
      guests
    };

    this.isSubmitting.set(true);
    this.errorMessage.set(null);

    const request$ = this.isCustomer()
      ? this.bookingService.createBooking(basePayload)
      : this.bookingService.createGuestBooking({
          ...basePayload,
          ospite: this.anonymousForm.getRawValue()
        });

    request$.subscribe({
      next: (booking) => {
        this.isSubmitting.set(false);
        this.router.navigate(['/prenotazioni', booking.id, 'pagamento'], {
          queryParams: { totalAmount: this.totalAmount() }
        });
      },
      error: (error: HttpErrorResponse) => {
        this.isSubmitting.set(false);
        if (error.status === 409) {
          this.errorMessage.set('Questa camera è già prenotata nelle date selezionate.');
        } else if (error.status === 400) {
          this.errorMessage.set('Controlla i dati inseriti e riprova.');
        } else if (error.status === 401 || error.status === 403) {
          this.errorMessage.set('Sessione non valida. Puoi uscire oppure continuare come ospite.');
        } else {
          this.errorMessage.set('Non è stato possibile salvare la prenotazione nel database.');
        }
      }
    });
  }

  private createGuestGroup(): FormGroup {
    return this.fb.group({
      nome: ['', Validators.required],
      cognome: ['', Validators.required]
    });
  }
}
