package com.example.progettoalbergo.Controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

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
    public UserDashboardResponse dashboard(HttpServletRequest request) {
        Long userId = authenticatedCustomerId(request);
        Utente user = findUser(userId);

        List<UserBookingResponse> bookings = prenotazioneRepository
                .findByIdUtenteOrderByDataArrivoDesc(userId)
                .stream()
                .map(this::toBookingResponse)
                .toList();

        return new UserDashboardResponse(toProfileResponse(user), bookings);
    }

    @PatchMapping("/profile")
    public UserProfileResponse updateProfile(@RequestBody ProfileUpdateRequest payload,
            HttpServletRequest request) {
        Utente user = findUser(authenticatedCustomerId(request));

        user.setNome(required(payload.nome(), "Il nome e' obbligatorio"));
        user.setCognome(required(payload.cognome(), "Il cognome e' obbligatorio"));
        user.setUsername(required(payload.username(), "Lo username e' obbligatorio"));
        user.setCellulare(clean(payload.cellulare()));

        return toProfileResponse(utenteRepository.save(user));
    }

    @PatchMapping("/bookings/{bookingId}/nominativo")
    public UserBookingResponse updateBookingName(@PathVariable Long bookingId,
            @RequestBody BookingNameUpdateRequest payload,
            HttpServletRequest request) {
        Long userId = authenticatedCustomerId(request);
        PrenotazioneAlbergo booking = prenotazioneRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Prenotazione non trovata"));

        if (!userId.equals(booking.getidUtente())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Non puoi modificare questa prenotazione");
        }

        booking.setNominativo(required(payload.nominativo(), "Il nominativo e' obbligatorio"));
        return toBookingResponse(prenotazioneRepository.save(booking));
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

    private UserBookingResponse toBookingResponse(PrenotazioneAlbergo booking) {
        Camera camera = booking.getIdCamera() == null
                ? null
                : cameraRepository.findById(booking.getIdCamera()).orElse(null);

        Double pricePerNight = camera == null ? null : camera.getPrezzoPerNotte();
        Double estimatedTotal = estimateTotal(
                booking.getDataArrivo(), booking.getDataPartenza(), pricePerNight);

        return new UserBookingResponse(
                booking.getIdPrenotazioneAlbergo(),
                camera == null ? "Camera non disponibile" : camera.getNome(),
                camera == null ? null : camera.getImmagine(),
                booking.getIdCamera(),
                booking.getDataPrenotazione(),
                booking.getDataArrivo(),
                booking.getDataPartenza(),
                booking.getNominativo(),
                booking.isStato(),
                pricePerNight,
                estimatedTotal);
    }

    private Double estimateTotal(LocalDate arrival, LocalDate departure, Double pricePerNight) {
        if (arrival == null || departure == null || pricePerNight == null) {
            return null;
        }
        long nights = ChronoUnit.DAYS.between(arrival, departure);
        return nights <= 0 ? 0.0 : nights * pricePerNight;
    }

    private UserProfileResponse toProfileResponse(Utente user) {
        return new UserProfileResponse(
                user.getIdUtente(),
                user.getNome(),
                user.getCognome(),
                user.getUsername(),
                user.getCellulare(),
                user.getEmail());
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

    public record UserDashboardResponse(
            UserProfileResponse profile,
            List<UserBookingResponse> bookings) {
    }

    public record UserProfileResponse(
            Long idUtente,
            String nome,
            String cognome,
            String username,
            String cellulare,
            String email) {
    }

    public record UserBookingResponse(
            Long id,
            String camera,
            String immagine,
            Long idCamera,
            LocalDate dataPrenotazione,
            LocalDate dataArrivo,
            LocalDate dataPartenza,
            String nominativo,
            boolean confermata,
            Double prezzoPerNotte,
            Double totaleStimato) {
    }

    public record ProfileUpdateRequest(
            String nome,
            String cognome,
            String username,
            String cellulare) {
    }

    public record BookingNameUpdateRequest(String nominativo) {
    }
}
