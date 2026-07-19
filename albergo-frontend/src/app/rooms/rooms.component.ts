import { Component, inject, signal } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators
} from '@angular/forms';
import { Room } from '../core/models/room.model';
import { RoomService } from '../core/services/room.service';
import { RoomCardComponent } from '../shared/room-card/room-card.component';

// Il check-out deve essere successivo al check-in
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
  imports: [ReactiveFormsModule, RoomCardComponent],
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
        this.errorMessage.set('Impossibile recuperare le stanze disponibili. Riprova.');
        this.isLoading.set(false);
        this.hasSearched.set(true);
      }
    });
  }

  // Passa le date scelte come queryParams: il form di prenotazione le userà per prefill/riepilogo
  bookingQueryParams(): Record<string, string> {
    const { checkIn, checkOut } = this.searchForm.getRawValue();
    return { checkIn, checkOut };
  }
}
