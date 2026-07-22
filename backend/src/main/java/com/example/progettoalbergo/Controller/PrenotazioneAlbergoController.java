/*
 * GUIDA DIDATTICA BACKEND
 * ---------------------------------------------------------------------------
 * CONTROLLER REST: riceve richieste HTTP, valida i dati di ingresso e coordina repository/servizi per produrre la risposta.
 * File: PrenotazioneAlbergoController.java
 * Segui le annotazioni Spring (@RestController, @Entity, @Service...) per capire
 * quale ruolo assume la classe nel flusso richiesta -> logica -> database.
 */
package com.example.progettoalbergo.Controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.progettoalbergo.Model.Ospite;
import com.example.progettoalbergo.Model.PrenotazioneAlbergo;
import com.example.progettoalbergo.Repository.CameraRepository;
import com.example.progettoalbergo.Repository.OspiteRepository;
import com.example.progettoalbergo.Repository.PrenotazioneAlbergoRepository;
import com.example.progettoalbergo.Repository.UtenteRepository;
import com.example.progettoalbergo.Security.JwtUtil;
import com.example.progettoalbergo.Services.PrenotazioneAlbergoHib;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/")
public class PrenotazioneAlbergoController {

    private final PrenotazioneAlbergoHib service;
    private final PrenotazioneAlbergoRepository repository;
    private final CameraRepository cameraRepository;
    private final OspiteRepository ospiteRepository;
    private final UtenteRepository utenteRepository;
    private final JwtUtil jwtUtil;

    public PrenotazioneAlbergoController(PrenotazioneAlbergoHib service,
            PrenotazioneAlbergoRepository repository,
            CameraRepository cameraRepository,
            OspiteRepository ospiteRepository,
            UtenteRepository utenteRepository,
            JwtUtil jwtUtil) {
        this.service = service;
        this.repository = repository;
        this.cameraRepository = cameraRepository;
        this.ospiteRepository = ospiteRepository;
        this.utenteRepository = utenteRepository;
        this.jwtUtil = jwtUtil;
    }

    // Prenotazione online dell'utente autenticato.
    // Non chiediamo i dati degli accompagnatori: il nominativo è l'intestatario
    // dell'account e il frontend invia solo il numero totale di persone.
    @PostMapping("/prenotazioni")
    public Map<String, Object> createBooking(@RequestBody Map<String, Object> payload,
            HttpServletRequest request) {
        Long userId = authenticatedCustomerId(request);

        Long roomId = asLong(payload == null ? null : payload.get("roomId"));
        LocalDate checkIn = asDate(payload == null ? null : payload.get("checkIn"));
        LocalDate checkOut = asDate(payload == null ? null : payload.get("checkOut"));
        int numeroOspiti = asInt(payload == null ? null : payload.get("numeroOspiti"));

        validateBooking(roomId, checkIn, checkOut);
        if (numeroOspiti < 1 || numeroOspiti > 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Il numero di ospiti deve essere compreso tra 1 e 8");
        }

        var utente = utenteRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Utente non trovato"));
        String nominativo = (clean(utente.getNome()) + " " + clean(utente.getCognome())).trim();
        if (nominativo.isBlank()) {
            nominativo = clean(utente.getUsername());
        }

        PrenotazioneAlbergo booking = new PrenotazioneAlbergo();
        booking.setidUtente(userId);
        booking.setIdOspite(null);
        booking.setIdCamera(roomId);
        booking.setDataPrenotazione(LocalDate.now());
        booking.setDataArrivo(checkIn);
        booking.setDataPartenza(checkOut);
        booking.setNominativo(nominativo);
        booking.setNumeroOspiti(numeroOspiti);
        booking.setOrigine("ONLINE_UTENTE");
        booking.setStato(false);

        PrenotazioneAlbergo saved = repository.save(booking);
        return bookingResponse(saved);
    }

    // Prenotazione online senza account.
    // Si memorizzano solo i dati dell'intestatario e il numero totale di persone:
    // non vengono richiesti nome/cognome degli altri partecipanti.
    @PostMapping("/prenotazioni/ospite")
    public Map<String, Object> createGuestBooking(@RequestBody Map<String, Object> payload) {
        Long roomId = asLong(payload == null ? null : payload.get("roomId"));
        LocalDate checkIn = asDate(payload == null ? null : payload.get("checkIn"));
        LocalDate checkOut = asDate(payload == null ? null : payload.get("checkOut"));
        int numeroOspiti = asInt(payload == null ? null : payload.get("numeroOspiti"));
        Map<String, String> guestData = stringMap(payload == null ? null : payload.get("ospite"));

        validateBooking(roomId, checkIn, checkOut);
        if (numeroOspiti < 1 || numeroOspiti > 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Il numero di ospiti deve essere compreso tra 1 e 8");
        }

        String nome = clean(guestData.get("nome"));
        String cognome = clean(guestData.get("cognome"));
        String cellulare = clean(guestData.get("cellulare"));
        String email = clean(guestData.get("email"));
        if (nome.isBlank() || cognome.isBlank() || cellulare.isBlank() || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nome, cognome, cellulare ed email dell'intestatario sono obbligatori");
        }

        Ospite ospite = new Ospite();
        ospite.setNome(nome);
        ospite.setCognome(cognome);
        ospite.setCellulare(cellulare);
        ospite.setEmail(email.toLowerCase());
        Ospite savedGuest = ospiteRepository.save(ospite);

        PrenotazioneAlbergo booking = new PrenotazioneAlbergo();
        booking.setidUtente(null);
        booking.setIdOspite(savedGuest.getIdOspite());
        booking.setIdCamera(roomId);
        booking.setDataPrenotazione(LocalDate.now());
        booking.setDataArrivo(checkIn);
        booking.setDataPartenza(checkOut);
        booking.setNominativo((nome + " " + cognome).trim());
        booking.setNumeroOspiti(numeroOspiti);
        booking.setOrigine("ONLINE_OSPITE");
        booking.setStato(false);

        PrenotazioneAlbergo saved = repository.save(booking);
        Map<String, Object> response = bookingResponse(saved);
        response.put("ospiteId", savedGuest.getIdOspite());
        return response;
    }

    // Endpoint CRUD originali mantenuti per non interrompere il lavoro dei colleghi.
    @GetMapping("/prenotazionealbergo")
    public List<PrenotazioneAlbergo> readAll() {
        return service.trovaTutti();
    }

    @GetMapping("/prenotazionealbergo/{id}")
    public PrenotazioneAlbergo readOne(@PathVariable Long id) {
        return service.trovaId(id);
    }

    @PostMapping("/prenotazionealbergo")
    public PrenotazioneAlbergo add(@RequestBody PrenotazioneAlbergo booking) {
        return service.salva(booking);
    }

    @PutMapping("/prenotazionealbergo/{id}")
    public PrenotazioneAlbergo update(@PathVariable Long id,
            @RequestBody PrenotazioneAlbergo booking) {
        booking.setIdPrenotazioneAlbergo(id);
        return service.salva(booking);
    }

    @DeleteMapping("/prenotazionealbergo/{id}")
    public void delete(@PathVariable Long id) {
        service.elimina(id);
    }

    private void validateBooking(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        if (roomId == null || checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date o camera non valide");
        }
        if (!cameraRepository.existsById(roomId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Camera non trovata");
        }

        boolean occupied = repository.findAll().stream()
                .filter(item -> roomId.equals(item.getIdCamera()))
                .filter(item -> item.getDataArrivo() != null && item.getDataPartenza() != null)
                .anyMatch(item -> item.getDataArrivo().isBefore(checkOut)
                        && item.getDataPartenza().isAfter(checkIn));
        if (occupied) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Camera gia' prenotata nelle date selezionate");
        }
    }

    private Map<String, Object> bookingResponse(PrenotazioneAlbergo saved) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", saved.getIdPrenotazioneAlbergo());
        response.put("roomId", saved.getIdCamera());
        response.put("checkIn", saved.getDataArrivo());
        response.put("checkOut", saved.getDataPartenza());
        response.put("numeroOspiti", saved.getNumeroOspiti());
        response.put("origine", saved.getOrigine());
        response.put("stato", saved.isStato() ? "CONFERMATA" : "IN_ATTESA");
        return response;
    }

    private Long authenticatedCustomerId(HttpServletRequest request) {
        String token = bearerToken(request);
        if (!"CLIENTE".equals(jwtUtil.extractRole(token))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "La prenotazione e' riservata ai clienti");
        }
        Long userId = jwtUtil.extractUserId(token);
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utente non valido");
        }
        return userId;
    }

    private String bearerToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token mancante");
        }
        String token = authorization.substring(7).trim();
        if (token.isBlank() || !jwtUtil.isValid(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Token non valido o scaduto");
        }
        return token;
    }

    private Long asLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value instanceof String stringValue && !stringValue.isBlank()) {
            try {
                return Long.valueOf(stringValue);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private int asInt(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value != null) {
            try {
                return Integer.parseInt(String.valueOf(value));
            } catch (NumberFormatException ignored) {
                return 0;
            }
        }
        return 0;
    }

    private LocalDate asDate(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return LocalDate.parse(String.valueOf(value));
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private List<Map<String, String>> guests(Object value) {
        if (!(value instanceof List<?> rawGuests)) {
            return List.of();
        }

        List<Map<String, String>> result = new ArrayList<>();
        for (Object rawGuest : rawGuests) {
            if (!(rawGuest instanceof Map<?, ?> rawMap)) {
                continue;
            }
            Map<String, String> guest = new LinkedHashMap<>();
            guest.put("nome", rawMap.get("nome") == null ? "" : String.valueOf(rawMap.get("nome")));
            guest.put("cognome", rawMap.get("cognome") == null ? "" : String.valueOf(rawMap.get("cognome")));
            result.add(guest);
        }
        return result;
    }

    private Map<String, String> stringMap(Object value) {
        if (!(value instanceof Map<?, ?> rawMap)) {
            return Map.of();
        }
        Map<String, String> result = new LinkedHashMap<>();
        rawMap.forEach((key, rawValue) ->
                result.put(String.valueOf(key), rawValue == null ? "" : String.valueOf(rawValue)));
        return result;
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
