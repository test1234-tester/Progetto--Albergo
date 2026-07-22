export interface Room {
  idCamera: number;
  nome: string;
  descrizione: string;
  immagine: string;
  prezzoPerNotte?: number;
  stato?: boolean;
  occupanti?: number;
}
