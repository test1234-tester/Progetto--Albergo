import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

export interface ServizioOpzionale {
  id: number;
  nome: string;
  prezzo: number;
}

export interface Room {
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
  searchForm!: FormGroup;
  isLoading = signal<boolean>(false);
  
  selectedRoom = signal<Room | null>(null);
  serviziOpzionaliScelti = signal<ServizioOpzionale[]>([]);
  metodoPagamento = signal<'carta' | 'bonifico'>('carta');

  // Segnale per gestire la visibilità del CVC
  mostraCvc = signal<boolean>(false);

  // Gestione schermata di conferma e registrazione rapida
  prenotazioneConfermata = signal<boolean>(false);
  codicePrenotazione = signal<string>('');
  emailCliente = signal<string>('mario.rossi@gmail.com');

  // Tutte le 50 stanze del sistema
  allRooms = signal<Room[]>([]);
  
  // Stanze visualizzate
  filteredRooms = signal<Room[]>([]);

  serviziOpzionaliDisponibili: ServizioOpzionale[] = [
    { id: 1, nome: 'Colazione Gourmet in Camera', prezzo: 15 },
    { id: 2, nome: 'Mezza Pensione (Colazione e Cena - al giorno/persona)', prezzo: 35 },
    { id: 3, nome: 'Pensione Completa (Colazione, Pranzo e Cena - al giorno/persona)', prezzo: 60 },
    { id: 4, nome: 'Posto Auto Riservato Garage', prezzo: 20 },
    { id: 5, nome: 'Late Check-out (fino alle 14:00)', prezzo: 35 },
    { id: 6, nome: 'Bottiglia di Champagne di Benvenuto', prezzo: 60 }
  ];

  constructor(private fb: FormBuilder) {}

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

    this.genera50Stanze();
  }

  genera50Stanze(): void {
    const tipi = ['Standard', 'Deluxe', 'Junior Suite', 'Suite Panorama', 'Executive Suite'];
    const stanze: Room[] = [];

    for (let i = 1; i <= 50; i++) {
      const tipo = tipi[i % tipi.length];
      const capienzaCalcolata = (i % 4) + 2;

      stanze.push({
        idCamera: i,
        nome: `${tipo} Camera ${i}`,
        prezzoNotte: 80 + (i % 5) * 25,
        capienza: capienzaCalcolata,
        serviziFissi: ['Wi-Fi 6', 'Aria Condizionata', 'Smart TV', 'Minibar'],
        descrizione: `Elegante ${tipo.toLowerCase()} per massimo ${capienzaCalcolata} ospiti con tutti i comfort moderni.`
      });
    }

    this.allRooms.set(stanze);
    this.filteredRooms.set(stanze);
  }

  onImgError(event: Event): void {
    const imgElement = event.target as HTMLImageElement;
    imgElement.src = 'camere/camera-1.jpg';
  }

  notti = computed(() => {
    const checkIn = new Date(this.searchForm?.get('checkIn')?.value);
    const checkOut = new Date(this.searchForm?.get('checkOut')?.value);
    if (!checkIn || !checkOut || checkOut <= checkIn) return 1;
    const diffTime = Math.abs(checkOut.getTime() - checkIn.getTime());
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24)) || 1;
  });

  costoTotale = computed(() => {
    const room = this.selectedRoom();
    if (!room) return 0;

    let totale = room.prezzoNotte * this.notti();
    if (this.searchForm.get('pacchettoSpa')?.value) totale += 200;

    const adulti = Number(this.searchForm.get('adulti')?.value) || 1;
    const bambini = Number(this.searchForm.get('bambini')?.value) || 0;
    const totaleOspiti = adulti + bambini;

    const costoServizi = this.serviziOpzionaliScelti().reduce((acc, s) => {
      if (s.id === 2 || s.id === 3) {
        return acc + (s.prezzo * this.notti() * totaleOspiti);
      }
      return acc + s.prezzo;
    }, 0);

    return totale + costoServizi;
  });

  caparra = computed(() => this.costoTotale() * 0.10);
  saldoRimanente = computed(() => this.costoTotale() - this.caparra());

  onSearch(): void {
    if (this.searchForm.invalid) return;

    this.isLoading.set(true);
    this.selectedRoom.set(null);

    const adulti = Number(this.searchForm.get('adulti')?.value) || 0;
    const bambini = Number(this.searchForm.get('bambini')?.value) || 0;
    const totaleOspiti = adulti + bambini;

    setTimeout(() => {
      const risultanti = this.allRooms().filter(room => room.capienza >= totaleOspiti);
      this.filteredRooms.set(risultanti);
      this.isLoading.set(false);
    }, 400);
  }

  selezionaStanza(room: Room): void {
    this.selectedRoom.set(room);
    this.serviziOpzionaliScelti.set([]);
  }

  deselezionaStanza(): void {
    this.selectedRoom.set(null);
  }

  toggleServizioOpzionale(servizio: ServizioOpzionale, event: Event): void {
    const checked = (event.target as HTMLInputElement).checked;
    if (checked) {
      this.serviziOpzionaliScelti.update(lista => [...lista, servizio]);
    } else {
      this.serviziOpzionaliScelti.update(lista => lista.filter(s => s.id !== servizio.id));
    }
  }

  toggleMostraCvc(): void {
    this.mostraCvc.update(v => !v);
  }

  confermaEPay(): void {
    const codiceRandom = 'LUX-' + Math.floor(1000 + Math.random() * 9000);
    this.codicePrenotazione.set(codiceRandom);
    this.prenotazioneConfermata.set(true);
  }

  nuovaPrenotazione(): void {
    this.prenotazioneConfermata.set(false);
    this.selectedRoom.set(null);
    this.serviziOpzionaliScelti.set([]);
  }
}