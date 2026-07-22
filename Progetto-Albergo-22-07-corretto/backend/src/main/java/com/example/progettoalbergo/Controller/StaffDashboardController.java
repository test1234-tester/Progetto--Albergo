package com.example.progettoalbergo.Controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.progettoalbergo.Model.Camera;
import com.example.progettoalbergo.Model.Ospite;
import com.example.progettoalbergo.Model.PrenotazioneAlbergo;
import com.example.progettoalbergo.Model.Utente;
import com.example.progettoalbergo.Repository.CameraRepository;
import com.example.progettoalbergo.Repository.OspiteRepository;
import com.example.progettoalbergo.Repository.PagamentoRepository;
import com.example.progettoalbergo.Repository.PrenotazioneAlbergoRepository;
import com.example.progettoalbergo.Repository.PrenotazioneServizioRepository;
import com.example.progettoalbergo.Repository.UtenteRepository;
import com.example.progettoalbergo.Security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/staff")
public class StaffDashboardController {

    private final CameraRepository cameraRepository;
    private final PrenotazioneAlbergoRepository prenotazioneRepository;
    private final UtenteRepository utenteRepository;
    private final PrenotazioneServizioRepository prenotazioneServizioRepository;
    private final OspiteRepository ospiteRepository;
    private final PagamentoRepository pagamentoRepository;
    private final JwtUtil jwtUtil;

    public StaffDashboardController(CameraRepository cameraRepository,
            PrenotazioneAlbergoRepository prenotazioneRepository,
            PrenotazioneServizioRepository prenotazioneServizioRepository,
            UtenteRepository utenteRepository,
            OspiteRepository ospiteRepository,
            PagamentoRepository pagamentoRepository,
            JwtUtil jwtUtil) {
        this.cameraRepository = cameraRepository;
        this.prenotazioneRepository = prenotazioneRepository;
        this.prenotazioneServizioRepository = prenotazioneServizioRepository;
        this.utenteRepository = utenteRepository;
        this.ospiteRepository = ospiteRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard(HttpServletRequest request) {
        requireStaff(request);

        List<Map<String, Object>> bookings = prenotazioneRepository
                .findAllByOrderByDataArrivoAsc()
                .stream()
                .map(this::bookingResponse)
                .toList();
        List<Map<String, Object>> rooms = cameraRepository.findAll().stream()
                .sorted(Comparator.comparing(Camera::getIdCamera))
                .map(this::roomResponse)
                .toList();
        List<Map<String, Object>> users = utenteRepository.findAll().stream()
                .sorted(Comparator.comparing(Utente::getCognome,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .map(this::userResponse)
                .toList();

        long occupiedRooms = rooms.stream()
                .filter(room -> Boolean.TRUE.equals(room.get("occupata")))
                .count();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("camereTotali", cameraRepository.count());
        stats.put("camereOccupate", occupiedRooms);
        stats.put("prenotazioniTotali", prenotazioneRepository.count());
        stats.put("clientiRegistrati", utenteRepository.count());
        stats.put("pagamentiInPendenza", pagamentoRepository.countByStato(false));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("stats", stats);
        response.put("bookings", bookings);
        response.put("rooms", rooms);
        response.put("users", users);
        return response;
    }

    // Inserimento di una prenotazione ricevuta fisicamente/telefonicamente dalla reception.
    // Nessun account cliente richiesto e nessun DTO: viene salvato solo l'intestatario + numero ospiti.
    @PostMapping("/bookings")
    public Map<String, Object> createPhysicalBooking(@RequestBody Map<String, Object> payload,
            HttpServletRequest request) {
        requireStaff(request);

        Long roomId = asLong(payload == null ? null : payload.get("roomId"));
        LocalDate checkIn = asDate(payload == null ? null : payload.get("checkIn"));
        LocalDate checkOut = asDate(payload == null ? null : payload.get("checkOut"));
        int numeroOspiti = asInt(payload == null ? null : payload.get("numeroOspiti"));
        Map<String, String> guestData = stringMap(payload == null ? null : payload.get("ospite"));

        if (roomId == null || checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date o camera non valide");
        }
        if (!cameraRepository.existsById(roomId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Camera non trovata");
        }
        if (numeroOspiti < 1 || numeroOspiti > 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Il numero di ospiti deve essere compreso tra 1 e 8");
        }
        if (isRoomOccupied(roomId, checkIn, checkOut)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Camera gia' prenotata nelle date selezionate");
        }

        String nome = clean(guestData.get("nome"));
        String cognome = clean(guestData.get("cognome"));
        String cellulare = clean(guestData.get("cellulare"));
        String email = clean(guestData.get("email"));
        if (nome.isBlank() || cognome.isBlank() || cellulare.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nome, cognome e cellulare dell'intestatario sono obbligatori");
        }

        Ospite guest = new Ospite();
        guest.setNome(nome);
        guest.setCognome(cognome);
        guest.setCellulare(cellulare);
        guest.setEmail(email.isBlank() ? null : email.toLowerCase());
        Ospite savedGuest = ospiteRepository.save(guest);

        PrenotazioneAlbergo booking = new PrenotazioneAlbergo();
        booking.setidUtente(null);
        booking.setIdOspite(savedGuest.getIdOspite());
        booking.setIdCamera(roomId);
        booking.setDataPrenotazione(LocalDate.now());
        booking.setDataArrivo(checkIn);
        booking.setDataPartenza(checkOut);
        booking.setNominativo((nome + " " + cognome).trim());
        booking.setNumeroOspiti(numeroOspiti);
        booking.setOrigine("STRUTTURA");
        booking.setStato(true);

        return bookingResponse(prenotazioneRepository.save(booking));
    }

    @PatchMapping("/bookings/{bookingId}")
    public Map<String, Object> updateBooking(@PathVariable Long bookingId,
            @RequestBody Map<String, Object> payload,
            HttpServletRequest request) {
        requireStaff(request);
        PrenotazioneAlbergo booking = prenotazioneRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Prenotazione non trovata"));

        Long roomId = asLong(payload == null ? null : payload.get("roomId"));
        LocalDate checkIn = asDate(payload == null ? null : payload.get("checkIn"));
        LocalDate checkOut = asDate(payload == null ? null : payload.get("checkOut"));
        int numeroOspiti = asInt(payload == null ? null : payload.get("numeroOspiti"));
        String nominativo = clean(payload == null || payload.get("nominativo") == null
                ? null : String.valueOf(payload.get("nominativo")));

        if (roomId == null || !cameraRepository.existsById(roomId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Camera non valida");
        }
        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date non valide");
        }
        if (numeroOspiti < 1 || numeroOspiti > 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Il numero di ospiti deve essere compreso tra 1 e 8");
        }
        if (nominativo.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il nominativo e' obbligatorio");
        }
        if (isRoomOccupied(roomId, checkIn, checkOut, bookingId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Camera gia' prenotata nelle date selezionate");
        }

        booking.setIdCamera(roomId);
        booking.setDataArrivo(checkIn);
        booking.setDataPartenza(checkOut);
        booking.setNumeroOspiti(numeroOspiti);
        booking.setNominativo(nominativo);
        if (payload != null && payload.containsKey("confermata")) {
            booking.setStato(asBoolean(payload.get("confermata")));
        }
        return bookingResponse(prenotazioneRepository.save(booking));
    }

    // Endpoint mantenuto per retrocompatibilita' con eventuali versioni precedenti del frontend.
    @PatchMapping("/bookings/{bookingId}/status")
    public Map<String, Object> updateBookingStatus(@PathVariable Long bookingId,
            @RequestBody Map<String, Object> payload,
            HttpServletRequest request) {
        requireStaff(request);
        PrenotazioneAlbergo booking = prenotazioneRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Prenotazione non trovata"));
        booking.setStato(asBoolean(payload == null ? null : payload.get("confermata")));
        return bookingResponse(prenotazioneRepository.save(booking));
    }

    @Transactional
    @DeleteMapping("/bookings/{bookingId}")
    public void deleteBooking(@PathVariable Long bookingId, HttpServletRequest request) {
        requireStaff(request);
        PrenotazioneAlbergo booking = prenotazioneRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Prenotazione non trovata"));
        Long guestId = booking.getIdOspite();
        prenotazioneServizioRepository.deleteByIdPrenotazioneAlbergo(bookingId);
        prenotazioneRepository.delete(booking);
        if (guestId != null && prenotazioneRepository.countByIdOspite(guestId) == 0) {
            ospiteRepository.deleteById(guestId);
        }
    }

    @PatchMapping("/rooms/{roomId}/status")
    public Map<String, Object> updateRoomStatus(@PathVariable Long roomId,
            @RequestBody Map<String, Object> payload,
            HttpServletRequest request) {
        requireStaff(request);
        Camera camera = cameraRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Camera non trovata"));

        boolean occupied = asBoolean(payload == null ? null : payload.get("occupata"));
        int occupants = asInt(payload == null ? null : payload.get("occupanti"));
        camera.setStato(occupied);
        camera.setOccupanti(occupied ? Math.max(1, occupants) : 0);
        return roomResponse(cameraRepository.save(camera));
    }

    private Map<String, Object> bookingResponse(PrenotazioneAlbergo booking) {
        Camera camera = booking.getIdCamera() == null ? null
                : cameraRepository.findById(booking.getIdCamera()).orElse(null);
        Utente user = booking.getidUtente() == null ? null
                : utenteRepository.findById(booking.getidUtente()).orElse(null);
        Ospite guest = booking.getIdOspite() == null ? null
                : ospiteRepository.findById(booking.getIdOspite()).orElse(null);

        String cliente = user != null ? fullName(user)
                : guest != null ? fullName(guest) : "Cliente non disponibile";
        String email = user != null ? user.getEmail()
                : guest != null && guest.getEmail() != null ? guest.getEmail() : "";
        String origine = booking.getOrigine();
        if (origine == null || origine.isBlank()) {
            origine = booking.getidUtente() != null ? "ONLINE_UTENTE"
                    : booking.getIdOspite() != null ? "ONLINE_OSPITE" : "NON_SPECIFICATA";
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", booking.getIdPrenotazioneAlbergo());
        response.put("idUtente", booking.getidUtente());
        response.put("idOspite", booking.getIdOspite());
        response.put("cliente", cliente);
        response.put("email", email);
        response.put("camera", camera == null ? "Camera non disponibile" : camera.getNome());
        response.put("numeroCamera", booking.getIdCamera());
        response.put("dataArrivo", booking.getDataArrivo());
        response.put("dataPartenza", booking.getDataPartenza());
        response.put("nominativo", booking.getNominativo());
        response.put("numeroOspiti", booking.getNumeroOspiti() > 0 ? booking.getNumeroOspiti() : 1);
        response.put("origine", origine);
        response.put("confermata", booking.isStato());
        return response;
    }

    private Map<String, Object> roomResponse(Camera room) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", room.getIdCamera());
        response.put("numero", room.getIdCamera());
        response.put("nome", room.getNome());
        response.put("prezzoPerNotte", room.getPrezzoPerNotte());
        response.put("occupata", room.isStato());
        response.put("occupanti", room.getOccupanti());
        return response;
    }

    private Map<String, Object> userResponse(Utente user) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", user.getIdUtente());
        response.put("nominativo", fullName(user));
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("cellulare", user.getCellulare());
        return response;
    }

    private boolean isRoomOccupied(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        return isRoomOccupied(roomId, checkIn, checkOut, null);
    }

    private boolean isRoomOccupied(Long roomId, LocalDate checkIn, LocalDate checkOut, Long excludedBookingId) {
        return prenotazioneRepository.findAll().stream()
                .filter(item -> excludedBookingId == null
                        || !excludedBookingId.equals(item.getIdPrenotazioneAlbergo()))
                .filter(item -> roomId.equals(item.getIdCamera()))
                .filter(item -> item.getDataArrivo() != null && item.getDataPartenza() != null)
                .anyMatch(item -> item.getDataArrivo().isBefore(checkOut)
                        && item.getDataPartenza().isAfter(checkIn));
    }

    private String fullName(Utente user) {
        return ((user.getNome() == null ? "" : user.getNome()) + " "
                + (user.getCognome() == null ? "" : user.getCognome())).trim();
    }

    private String fullName(Ospite guest) {
        return ((guest.getNome() == null ? "" : guest.getNome()) + " "
                + (guest.getCognome() == null ? "" : guest.getCognome())).trim();
    }

    private Long asLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value != null) {
            try {
                return Long.valueOf(String.valueOf(value));
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private LocalDate asDate(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return LocalDate.parse(String.valueOf(value));
        } catch (DateTimeParseException ignored) {
            return null;
        }
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

    private boolean asBoolean(Object value) {
        if (value instanceof Boolean booleanValue) {
            return booleanValue;
        }
        return value != null && Boolean.parseBoolean(String.valueOf(value));
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

    private void requireStaff(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token mancante");
        }
        String token = authorization.substring(7).trim();
        if (token.isBlank() || !jwtUtil.isValid(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Token non valido o scaduto");
        }
        if (!"STAFF".equals(jwtUtil.extractRole(token))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Area riservata allo staff");
        }
    }
}
