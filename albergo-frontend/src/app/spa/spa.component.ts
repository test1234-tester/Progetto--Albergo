import { Component, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

// Importazione dell'interfaccia dal file separato "slot-spa.ts" (risalendo di un livello)
import { SlotSpa } from '../slot-spa';

interface SpaItem {
  title: string;
  tag: string;
  img: string;
  duration: string;
  temp: string;
  text: string;
  features: string[];
}

@Component({
  selector: 'app-spa',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './spa.component.html',
  styleUrl: './spa.component.scss'
})
export class SpaComponent implements OnDestroy {
  currentCategory: 'human' | 'pet' = 'human';
  currentIndex: number = 0;

  // Gestione Video Tour (Hover)
  isVideoTourActive: boolean = false;
  private videoInterval: any = null;
  private savedIndex: number = 0;

  // Gestione Modal Prenotazione
  isModalOpen: boolean = false;
  isConfirmed: boolean = false;

  // Carrello per prenotazioni multiple SPA
  slotSelezionati: SlotSpa[] = [];

  // Input temporanei per aggiungere uno slot
  nuovoSlot: SlotSpa = {
    data: '',
    orarioInizio: '',
    orarioFine: ''
  };

  // Form Model Pulito e Aggiornato
  bookingData = {
    nome: '',
    email: '',
    
    // Modalità: 'hotel' (Abbina al soggiorno) oppure 'spa-only' (Solo SPA online)
    tipoPrenotazione: 'hotel', 
    codicePrenotazione: '', 

    // Pagamento Diretto SPA ('card' oppure 'bonifico')
    metodoPagamento: 'card', 
    cardHolder: '',
    cardNumber: '',
    cardExpiry: '',
    cardCvc: '',

    // Gestione Pet SPA
    haPet: false,
    tipoPet: '',
    nomePet: '',
    razzaPet: '',
    tagliaPet: ''
  };

  // Database Servizi SPA
  db: { human: SpaItem[], pet: SpaItem[] } = {
    human: [
      {
        title: "Piscina Infinity Vista Mare",
        tag: "Outdoor Area",
        img: "Gemini_Generated_Image_ukvqepukvqepukvq.png",
        duration: "Illimitata",
        temp: "29°C",
        text: "La nostra magnifica piscina a sfioro all'aperto ti regalerà l'illusione di nuotare sospeso tra il cielo e il Mar Ligure. Dotata di idromassaggio a bordo sfioro e solarium con vista sul promontorio.",
        features: ["Idromassaggio integrato", "Zona Solarium King Size", "Accesso libero continuo"]
      },
      {
        title: "Vasca Idromassaggio Panoramica",
        tag: "Hydrotherapy",
        img: "Gemini_Generated_Image_as8p13as8p13as8p.png",
        duration: "Consigliati 30 min",
        temp: "36°C",
        text: "Immersa tra vetrate a tutta altezza che dominano sia le montagne della Liguria che il mare. I getti d'acqua differenziati rilassano le tensioni muscolari regalando un benessere profondo.",
        features: ["Postazioni ergonomiche", "Cascate cervicali", "Vista duale Mare & Monti"]
      },
      {
        title: "Grotta di Sale Himalayano",
        tag: "Halotherapy",
        img: "Gemini_Generated_Image_xmx7xyxmx7xyxmx7.png",
        duration: "45 min",
        temp: "24°C",
        text: "Un microclima unico con pareti in puro sale rosa illuminato. Purifica le vie respiratorie e la pelle grazie alla nebulizzazione di microparticelle saline ionizzate.",
        features: ["Nebulizzazione secca di sale", "Cromoterapia rilassante", "Sedute anatomiche riscaldate"]
      },
      {
        title: "Sauna Finlandese Vista Mare",
        tag: "Thermotherapy",
        img: "Gemini_Generated_Image_ukvqepukvqepukvq (1).png",
        duration: "15 min a sessione",
        temp: "85°C",
        text: "Realizzata in pregiato legno di cedro con un'ampia vetrata affacciata sulle acque di Finale Ligure. Favorisce la disintossicazione profonda e stimola la circolazione.",
        features: ["Stufa in pietra lavica", "Essenze aromatiche pino & eucalipto", "Doccia di reazione adiacente"]
      },
      {
        title: "Cabina Massaggi Olistici",
        tag: "Relaxation",
        img: "Gemini_Generated_Image_ukvqepukvqepukvq (2).png",
        duration: "Su richiesta",
        temp: "22°C",
        text: "Ambiente intimo e profumato dove i nostri terapisti eseguono trattamenti personalizzati e massaggi olistici fronte mare per ristabilire l'equilibrio di corpo e mente.",
        features: ["Olii biologici della Riviera", "Lettini termici regolabili", "Musica e aroma personalizzati"]
      },
      {
        title: "Area Relax & Lounge Tisana",
        tag: "Lounge Area",
        img: "Gemini_Generated_Image_ukvqepukvqepukvq (3).png",
        duration: "Illimitata",
        temp: "23°C",
        text: "Il luogo perfetto per concludere il percorso. Rilassati su chaise-longue eleganti sorseggiando tisane biologiche, infusi freddi e frutta fresca di stagione.",
        features: ["Infusi e tisane bio incluse", "Frutta fresca di stagione", "Ambiente insonorizzato"]
      }
    ],
    pet: [
      {
        title: "Piscina Idroterapia Canina",
        tag: "Pet Hydrotherapy",
        img: "spa-cani-piscina.png",
        duration: "Illimitata",
        temp: "28°C",
        text: "Vasca circolare panoramica riscaldata con gradini di ingresso facilitati...",
        features: ["Idromassaggio a pressione regolabile", "Assistente Pet dedicato", "Superficie in teak antiscivolo"]
      },
      {
        title: "Area Massaggi & Fisioterapia Pet",
        tag: "Pet Wellness",
        img: "spa-cani-massaggio.png",
        duration: "30 min",
        temp: "23°C",
        text: "Lettini imbottiti panoramici pensati per cani e gatti...",
        features: ["Operatori certificati Pet", "Asciugamani caldi Paws & Relax", "Musica rilassante per animali"]
      },
      {
        title: "Vasca Trattamenti Ozonoterapia & Bagno",
        tag: "Pet Grooming",
        img: "spa-cani-trattamento.png",
        duration: "45 min",
        temp: "32°C",
        text: "Bagni rigeneranti con microbolle all'ozono...",
        features: ["Trattamenti idratanti e dermoprotettivi", "Microbolle idromassaggio", "Shampoo bio delicato"]
      },
      {
        title: "Lounge Relax & Custodia Pet VIP",
        tag: "Pet Custody",
        img: "spa-cani-relax.png",
        duration: "Illimitata",
        temp: "22°C",
        text: "Mentre ti godi la SPA principale, i tuoi amici a quattro zampe riposano...",
        features: ["Custodia VIP gratuita inclusa", "Snack biologici e ciotole di design", "Brandine traspiranti King Size"]
      }
    ]
  };

  get currentList(): SpaItem[] {
    return this.db[this.currentCategory];
  }

  get currentItem(): SpaItem {
    return this.currentList[this.currentIndex];
  }

  switchCategory(cat: 'human' | 'pet'): void {
    this.currentCategory = cat;
    this.currentIndex = 0;
  }

  selectSlide(index: number): void {
    this.currentIndex = index;
  }

  navigateCarousel(direction: number): void {
    const len = this.currentList.length;
    this.currentIndex = (this.currentIndex + direction + len) % len;
  }

  // --- LOGICA GESTIONE SLOT MULTIPLI ---
  aggiungiSlot(): void {
    if (!this.nuovoSlot.data || !this.nuovoSlot.orarioInizio || !this.nuovoSlot.orarioFine) {
      alert("Seleziona data, orario di inizio e orario di fine prima di aggiungere la prenotazione.");
      return;
    }

    // Aggiunge lo slot all'array
    this.slotSelezionati.push({ ...this.nuovoSlot });

    // Reset degli input
    this.nuovoSlot = {
      data: '',
      orarioInizio: '',
      orarioFine: ''
    };
  }

  rimuoviSlot(index: number): void {
    this.slotSelezionati.splice(index, 1);
  }

  // --- LOGICA MODAL PRENOTAZIONE ---
  openBookingModal(): void {
    this.isModalOpen = true;
    this.isConfirmed = false;
  }

  closeModal(): void {
    this.isModalOpen = false;
  }

  submitBooking(): void {
    if (this.slotSelezionati.length === 0) {
      alert("Aggiungi almeno una prenotazione (Data e Orario) prima di procedere.");
      return;
    }

    if (this.bookingData.tipoPrenotazione === 'hotel') {
      
      // Controllo ID Prenotazione per addebito in camera
      if (!this.bookingData.codicePrenotazione.trim()) {
        alert("Per favore, inserisci il tuo Codice/ID Prenotazione dell'Albergo.");
        return;
      }

      const payloadSoggiorno = {
        nomeCliente: this.bookingData.nome,
        email: this.bookingData.email,
        idPrenotazioneAlbergo: this.bookingData.codicePrenotazione,
        importoAddebito: 200 * this.slotSelezionati.length,
        prenotazioni: this.slotSelezionati,
        dettagliPet: this.bookingData.haPet ? {
          tipo: this.bookingData.tipoPet,
          nome: this.bookingData.nomePet,
          razza: this.bookingData.razzaPet,
          taglia: this.bookingData.tagliaPet
        } : null
      };

      console.log('Invio Addebito Soggiorno con Slot Multipli:', payloadSoggiorno);
      this.isConfirmed = true;

    } else {
      // Gestione Solo SPA (Carta di Credito o Bonifico)
      this.gestisciPagamentoDiretto();
    }
  }

  private gestisciPagamentoDiretto(): void {
    if (this.bookingData.metodoPagamento === 'card') {
      // Validazione basica della carta
      if (!this.bookingData.cardNumber || !this.bookingData.cardHolder || !this.bookingData.cardCvc) {
        alert("Compila tutti i campi della carta di credito per proseguire.");
        return;
      }
    }

    const payloadPagamento = {
      nomeCliente: this.bookingData.nome,
      email: this.bookingData.email,
      metodo: this.bookingData.metodoPagamento,
      importo: 200 * this.slotSelezionati.length,
      prenotazioni: this.slotSelezionati,
      dettagliPet: this.bookingData.haPet ? {
        tipo: this.bookingData.tipoPet,
        nome: this.bookingData.nomePet,
        razza: this.bookingData.razzaPet,
        taglia: this.bookingData.tagliaPet
      } : null
    };

    console.log('Invio Pagamento Diretto con Slot Multipli:', payloadPagamento);
    this.isConfirmed = true;
  }

  ngOnDestroy(): void {
    if (this.videoInterval) {
      clearInterval(this.videoInterval);
    }
  }
}