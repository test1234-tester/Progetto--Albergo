# Nomi delle camere

Le camere non sono più mostrate come `Camera 1`, `Camera 2`, ecc.

Il progetto mantiene l'ID tecnico del database (1-50) per non rompere prenotazioni e foreign key, ma mostra un numero commerciale da hotel:

- ID 1 → Camera Comfort 101
- ID 2 → Camera Armonia 102
- ID 6 → Junior Suite 106
- ID 9 → Executive Spa Suite 109
- ID 10 → Camera Classic 110
- ID 11 → Camera Comfort 201
- ... fino al quinto piano.

## Perché non cambiamo gli ID

Gli ID sono chiavi tecniche già usate dalle prenotazioni. Cambiarli sarebbe inutile e rischioso. Cambiamo invece `nome` e il numero visualizzato.

## Database già esistente

`DataSeeder` ora esegue una piccola migrazione all'avvio: rinomina solo i valori ancora generici (`Camera 1`, `Camera 2`...), senza sovrascrivere eventuali nomi personalizzati.
