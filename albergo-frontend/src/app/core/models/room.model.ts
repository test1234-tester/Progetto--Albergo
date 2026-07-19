// Nomi campi allineati 1:1 all'entita' Camera del backend (progettoalbergo)
export interface Room {
  idCamera: number;
  nome: string;
  descrizione: string;
  immagine: string;
  prezzoPerNotte?: number;
}
