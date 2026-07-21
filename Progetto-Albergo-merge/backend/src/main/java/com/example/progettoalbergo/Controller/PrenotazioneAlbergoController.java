package com.example.progettoalbergo.Controller;

import java.time.LocalDate;
import java.util.List;

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

import com.example.progettoalbergo.Model.PrenotazioneAlbergo;
import com.example.progettoalbergo.Repository.CameraRepository;
import com.example.progettoalbergo.Repository.PrenotazioneAlbergoRepository;
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
    private final JwtUtil jwtUtil;

    public PrenotazioneAlbergoController(PrenotazioneAlbergoHib service,
            PrenotazioneAlbergoRepository repository,
            CameraRepository cameraRepository,
            JwtUtil jwtUtil) {
        this.service = service;
        this.repository = repository;
        this.cameraRepository = cameraRepository;
        this.jwtUtil = jwtUtil;
    }

    // Endpoint usato dal nuovo frontend: l'utente viene ricavato dal JWT.
    @PostMapping("/prenotazioni")
    public BookingResponse createBooking(@RequestBody BookingRequest payload,
            HttpServletRequest request) {
        Long userId = authenticatedCustomerId(request);

        if (payload == null || payload.roomId() == null || payload.checkIn() == null
                || payload.checkOut() == null || !payload.checkOut().isAfter(payload.checkIn())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date o camera non valide");
        }
        if (!cameraRepository.existsById(payload.roomId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Camera non trovata");
        }

        boolean occupied = repository.findAll().stream()
                .filter(item -> payload.roomId().equals(item.getIdCamera()))
                .filter(item -> item.getDataArrivo() != null && item.getDataPartenza() != null)
                .anyMatch(item -> item.getDataArrivo().isBefore(payload.checkOut())
                        && item.getDataPartenza().isAfter(payload.checkIn()));
        if (occupied) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Camera gia' prenotata nelle date selezionate");
        }

        String nominativo = payload.guests() == null ? "" : payload.guests().stream()
                .map(guest -> ((guest.nome() == null ? "" : guest.nome().trim()) + " "
                        + (guest.cognome() == null ? "" : guest.cognome().trim())).trim())
                .filter(value -> !value.isBlank())
                .reduce((first, second) -> first + ", " + second)
                .orElse("");
        if (nominativo.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Inserisci almeno un ospite");
        }

        PrenotazioneAlbergo booking = new PrenotazioneAlbergo();
        booking.setidUtente(userId);
        booking.setIdCamera(payload.roomId());
        booking.setDataPrenotazione(LocalDate.now());
        booking.setDataArrivo(payload.checkIn());
        booking.setDataPartenza(payload.checkOut());
        booking.setNominativo(nominativo);
        booking.setStato(false);

        PrenotazioneAlbergo saved = repository.save(booking);
        return new BookingResponse(
                saved.getIdPrenotazioneAlbergo(),
                saved.getIdCamera(),
                saved.getDataArrivo(),
                saved.getDataPartenza(),
                payload.guests(),
                "IN_ATTESA");
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

    public record GuestRequest(String nome, String cognome) {
    }

    public record BookingRequest(
            Long roomId,
            LocalDate checkIn,
            LocalDate checkOut,
            List<GuestRequest> guests) {
    }

    public record BookingResponse(
            Long id,
            Long roomId,
            LocalDate checkIn,
            LocalDate checkOut,
            List<GuestRequest> guests,
            String stato) {
    }
}
