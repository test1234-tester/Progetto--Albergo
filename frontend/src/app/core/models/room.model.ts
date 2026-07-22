/**
 * GUIDA DIDATTICA DEL FILE
 * ---------------------------------------------------------------------------
 * Tipi TypeScript delle camere.
 * Leggi prima gli import, poi @Component/@Injectable e infine campi e metodi:
 * questa è la stessa sequenza con cui Angular compone il comportamento del file.
 */
export interface Room {
  idCamera: number;
  nome: string;
  descrizione: string;
  immagine: string;
  prezzoPerNotte?: number;
  stato?: boolean;
  occupanti?: number;
}
