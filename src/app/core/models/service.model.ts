export interface HotelService {
  id: string;
  title: string;
  description: string;
  /** Valorizzato solo per servizi con un costo aggiuntivo fisso (es. Spa: 200€) */
  extraPrice?: number;
  /** true se il servizio è prenotabile anche indipendentemente dal soggiorno in albergo */
  bookableStandalone?: boolean;
}
