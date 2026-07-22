package com.example.progettoalbergo.Controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.progettoalbergo.Model.Camera;
import com.example.progettoalbergo.Model.PrenotazioneAlbergo;
import com.example.progettoalbergo.Model.Utente;
import com.example.progettoalbergo.Repository.CameraRepository;
import com.example.progettoalbergo.Repository.PrenotazioneAlbergoRepository;
import com.example.progettoalbergo.Repository.UtenteRepository;
import com.example.progettoalbergo.Security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/dashboard/user")
public class UserDashboardController {

    private final UtenteRepository utenteRepository;
    private final PrenotazioneAlbergoRepository prenotazioneRepository;
    private final CameraRepository cameraRepository;
    private final JwtUtil jwtUtil;

    public UserDashboardController(UtenteRepository utenteRepository,
            PrenotazioneAlbergoRepository prenotazioneRepository,
            CameraRepository cameraRepository,
            JwtUtil jwtUtil) {
        this.utenteRepository = utenteRepository;
        this.prenotazioneRepository = prenotazioneRepository;
        this.cameraRepository = cameraRepository;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public Map<String, Object> dashboard(HttpServletRequest request) {
        Long userId = authenticatedCustomerId(request);
        Utente user = findUser(userId);

        List<Map<String, Object>> bookings = prenotazioneRepository
                .findByIdUtenteOrderByDataArrivoDesc(userId)
                .stream()
                .map(this::bookingResponse)
                .toList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("profile", profileResponse(user));
        response.put("bookings", bookings);
        return response;
    }

    @PatchMapping("/profile")
    public Map<String, Object> updateProfile(@RequestBody Map<String, Object> payload,
            HttpServletRequest request) {
        Utente user = findUser(authenticatedCustomerId(request));

        user.setNome(required(text(payload, "nome"), "Il nome e' obbligatorio"));
        user.setCognome(required(text(payload, "cognome"), "Il cognome e' obbligatorio"));
        user.setUsername(required(text(payload, "username"), "Lo username e' obbligatorio"));
        user.setCellulare(clean(text(payload, "cellulare")));

        return profileResponse(utenteRepository.save(user));
    }

    @PatchMapping("/bookings/{bookingId}/nominativo")
    public Map<String, Object> updateBookingName(@PathVariable Long bookingId,
            @RequestBody Map<String, Object> payload,
            HttpServletRequest request) {
        Long userId = authenticatedCustomerId(request);
        PrenotazioneAlbergo booking = prenotazioneRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Prenotazione non trovata"));

        if (!userId.equals(booking.getidUtente())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Non puoi modificare questa prenotazione");
        }

        booking.setNominativo(required(text(payload, "nominativo"), "Il nominativo e' obbligatorio"));
        return bookingResponse(prenotazioneRepository.save(booking));
    }

    private Long authenticatedCustomerId(HttpServletRequest request) {
        String token = bearerToken(request);
        if (!"CLIENTE".equals(jwtUtil.extractRole(token))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Area riservata ai clienti");
        }
        Long userId = jwtUtil.extractUserId(token);
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Utente non presente nel token");
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

    private Utente findUser(Long userId) {
        return utenteRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Utente non trovato"));
    }

    private Map<String, Object> bookingResponse(PrenotazioneAlbergo booking) {
        Camera camera = booking.getIdCamera() == null
                ? null
                : cameraRepository.findById(booking.getIdCamera()).orElse(null);

        Double pricePerNight = camera == null ? null : camera.getPrezzoPerNotte();
        Double estimatedTotal = estimateTotal(
                booking.getDataArrivo(), booking.getDataPartenza(), pricePerNight);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", booking.getIdPrenotazioneAlbergo());
        response.put("camera", camera == null ? "Camera non disponibile" : camera.getNome());
        response.put("immagine", camera == null ? null : camera.getImmagine());
        response.put("idCamera", booking.getIdCamera());
        response.put("dataPrenotazione", booking.getDataPrenotazione());
        response.put("dataArrivo", booking.getDataArrivo());
        response.put("dataPartenza", booking.getDataPartenza());
        response.put("nominativo", booking.getNominativo());
        response.put("confermata", booking.isStato());
        response.put("prezzoPerNotte", pricePerNight);
        response.put("totaleStimato", estimatedTotal);
        return response;
    }

    private Double estimateTotal(LocalDate arrival, LocalDate departure, Double pricePerNight) {
        if (arrival == null || departure == null || pricePerNight == null) {
            return null;
        }
        long nights = ChronoUnit.DAYS.between(arrival, departure);
        return nights <= 0 ? 0.0 : nights * pricePerNight;
    }

    private Map<String, Object> profileResponse(Utente user) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idUtente", user.getIdUtente());
        response.put("nome", user.getNome());
        response.put("cognome", user.getCognome());
        response.put("username", user.getUsername());
        response.put("cellulare", user.getCellulare());
        response.put("email", user.getEmail());
        return response;
    }

    private String text(Map<String, Object> payload, String key) {
        if (payload == null || payload.get(key) == null) {
            return null;
        }
        return String.valueOf(payload.get(key));
    }

    private String required(String value, String message) {
        String cleaned = clean(value);
        if (cleaned.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
        return cleaned;
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
