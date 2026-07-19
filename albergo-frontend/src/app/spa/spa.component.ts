import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { SpaTreatment } from '../core/models/spa.model';
import { SpaService } from '../core/services/spa.service';

@Component({
  selector: 'app-spa',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './spa.component.html',
  styleUrl: './spa.component.scss'
})
export class SpaComponent implements OnInit {
  private readonly spaService = inject(SpaService);

  readonly treatments = signal<SpaTreatment[]>([]);
  readonly isLoading = signal(true);
  readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.spaService.getTreatments().subscribe({
      next: (treatments) => {
        this.treatments.set(treatments);
        this.isLoading.set(false);
      },
      error: () => {
        this.errorMessage.set('Impossibile caricare i trattamenti Spa al momento.');
        this.isLoading.set(false);
      }
    });
  }
}
