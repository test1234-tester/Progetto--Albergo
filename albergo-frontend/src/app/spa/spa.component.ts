import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

export interface SpaItem {
  id: number;
  tag: string;
  title: string;
  duration: string;
  temp: string;
  text: string;
  features: string[];
  img: string;
}

export interface SlotSpa {
  data: string;
  orarioInizio: string;
  orarioFine: string;
}

@Component({
  selector: 'app-spa',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './spa.component.html',
  styleUrl: './spa.component.scss'
})
export class SpaComponent {
  currentCategory: 'human' | 'pet' = 'human';
  currentIndex = 0;
  isModalOpen = false;
  isConfirmed = false;

  // Segnale per gestire la visibilità del CVC
  mostraCvc = signal<boolean>(false);

  // Codice univoco generato alla conferma
  codicePrenotazione = signal<string>('');

  humanList: SpaItem[] = [
    {
      id: 1,
      tag: 'Relax Muscolare',
      title: 'Idromassaggio Panorama Vista Mare',
      duration: '60 min',
      temp: '36°C - 38°C',
      text: 'Immergiti in una vasca idromassaggio affacciata sulle scogliere liguri per sciogliere ogni tensione muscolare.',
      features: ['Bocchette idromassaggio orientabili', 'Vista panoramica sul mare', 'Tisana detox inclusa'],
      img: '/spa1.png'
    },
    {
      id: 2,
      tag: 'Detox & Calore',
      title: 'Sauna Finlandese Panoramica',
      duration: '45 min',
      temp: '80°C - 90°C',
      text: 'Un calore avvolgente e secco con una splendida vetrata sul mare per favorire la circolazione e l’eliminazione delle tossine.',
      features: ['Legno pregiato', 'Aromaterapia alle essenze naturali', 'Doccia fredda di reazione'],
      img: '/spa2.png'
    },
    {
      id: 3,
      tag: 'Trattamenti Corpo',
      title: 'Area Massaggi & Trattamenti Esclusivi',
      duration: '50 min',
      temp: '24°C',
      text: 'Cabine trattamenti riservate dove terapisti esperti eseguono massaggi decontratturanti e trattamenti con olii essenziali.',
      features: ['Massaggi singoli o di coppia', 'Olii biologici certificati', 'Musica rilassante personalizzata'],
      img: '/spa3.png'
    },
    {
      id: 4,
      tag: 'Area Rest & Refresh',
      title: 'Lounge Bar & Zona Relax Vista Mare',
      duration: '30 min',
      temp: '22°C',
      text: 'Uno spazio elegante per riposarsi tra un percorso e l’altro, sorseggiando tisane biologiche ed estratti di frutta fresca.',
      features: ['Lettini anatomici', 'Buffet di frutta fresca e tisane', 'Servizio al tavolo'],
      img: '/spa4.png'
    },
    {
      id: 5,
      tag: 'Esperienza Aqua',
      title: 'Piscina Infinity Esterna',
      duration: '90 min',
      temp: '32°C',
      text: 'Nuota nella piscina a sfioro che si fonde con l’orizzonte del mare ligure.',
      features: ['Acqua riscaldata', 'Cascatelle cervicali', 'Bordi a sfioro infinity'],
      img: '/spa5.png'
    },
    {
      id: 6,
      tag: 'Purificazione',
      title: 'Grotta di Sale Naturale (Aloterapia)',
      duration: '45 min',
      temp: '21°C',
      text: 'Microclima salino per la rigenerazione delle vie respiratorie e il benessere della pelle.',
      features: ['Sale rosa dell’Himalaya', 'Haloterapia micronizzata', 'Cromoterapia rilassante'],
      img: '/spa6.png'
    }
  ];

  petList: SpaItem[] = [
    {
      id: 101,
      tag: 'Fisioterapia & Relax',
      title: 'Massaggio Rilassante per Cani',
      duration: '30 min',
      temp: '24°C',
      text: 'Massaggio delicato eseguito da operatori qualificati per distendere la muscolatura del tuo pet.',
      features: ['Trattamento decontratturante', 'Musica rilassante per animali', 'Olii pet-safe anallergici'],
      img: '/spa-cani-massaggio.png'
    },
    {
      id: 102,
      tag: 'Idroterapia Pet',
      title: 'Idromassaggio & Swim Canino',
      duration: '45 min',
      temp: '30°C',
      text: 'Vasca idromassaggio ad altezza regolabile con microbolle per stimolare la circolazione dei nostri amici a 4 zampe.',
      features: ['Vasca igienizzata ad uso esclusivo', 'Microbolle idromassaggio', 'Assistenza continua dell’operatore'],
      img: '/spa-cani-piscina.png'
    },
    {
      id: 103,
      tag: 'Relax Vista Mare',
      title: 'Area Solarium & Lounge Pet',
      duration: 'Illimitata',
      temp: '22°C',
      text: 'Lettini e brandine dedicate per far riposare il tuo cane in totale comfort dopo il trattamento.',
      features: ['Brandine ortopediche', 'Ciotole con acqua fresca filtrata', 'Snack biologici di benvenuto'],
      img: '/spa-cani-relax.png'
    },
    {
      id: 104,
      tag: 'Igiene & Cura',
      title: 'Bagno Terapeutico e Trattamento Pelo',
      duration: '40 min',
      temp: '32°C',
      text: 'Lavaggio delicato con shampoo bio nutriente e asciugatura ultra-silenziosa.',
      features: ['Shampoo e balsamo bio', 'Asciugatura no-stress silenziosa', 'Spazzolatura e lucidatura pelo'],
      img: '/spa-cani-trattamento.png'
    }
  ];

  nuovoSlot: SlotSpa = {
    data: new Date().toISOString().split('T')[0],
    orarioInizio: '10:00',
    orarioFine: '12:00'
  };

  slotSelezionati: SlotSpa[] = [];

  bookingData = {
    nome: '',
    email: '',
    tipoPrenotazione: 'spa-only',
    codicePrenotazione: '',
    metodoPagamento: 'card',
    cardHolder: '',
    cardNumber: '',
    cardExpiry: '',
    cardCvc: '',
    haPet: false,
    tipoPet: '',
    nomePet: '',
    razzaPet: '',
    tagliaPet: ''
  };

  get currentList(): SpaItem[] {
    return this.currentCategory === 'human' ? this.humanList : this.petList;
  }

  get currentItem(): SpaItem {
    return this.currentList[this.currentIndex] || this.currentList[0];
  }

  switchCategory(category: 'human' | 'pet'): void {
    this.currentCategory = category;
    this.currentIndex = 0;
  }

  navigateCarousel(direction: number): void {
    const total = this.currentList.length;
    this.currentIndex = (this.currentIndex + direction + total) % total;
  }

  selectSlide(index: number): void {
    this.currentIndex = index;
  }

  openBookingModal(): void {
    this.isModalOpen = true;
    this.isConfirmed = false;
  }

  closeModal(): void {
    this.isModalOpen = false;
  }

  toggleMostraCvc(): void {
    this.mostraCvc.update(v => !v);
  }

  aggiungiSlot(): void {
    if (this.nuovoSlot.data && this.nuovoSlot.orarioInizio && this.nuovoSlot.orarioFine) {
      this.slotSelezionati.push({ ...this.nuovoSlot });
    }
  }

  rimuoviSlot(index: number): void {
    this.slotSelezionati.splice(index, 1);
  }

  submitBooking(): void {
    const codice = 'SPA-' + Math.floor(1000 + Math.random() * 9000);
    this.codicePrenotazione.set(codice);
    this.isConfirmed = true;
  }
}