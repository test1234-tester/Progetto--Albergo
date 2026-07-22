package com.example.progettoalbergo.Controller;

import java.util.Comparator;
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
import com.example.progettoalbergo.Model.Ospite;
import com.example.progettoalbergo.Model.PrenotazioneAlbergo;
import com.example.progettoalbergo.Model.Utente;
import com.example.progettoalbergo.Repository.CameraRepository;
import com.example.progettoalbergo.Repository.OspiteRepository;
import com.example.progettoalbergo.Repository.PagamentoRepository;
import com.example.progettoalbergo.Repository.PrenotazioneAlbergoRepository;
import com.example.progettoalbergo.Repository.UtenteRepository;
import com.example.progettoalbergo.Security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/staff")
public class StaffDashboardController {

    private final CameraRepository cameraRepository;
    private final PrenotazioneAlbergoRepository prenotazioneRepository;
    private final UtenteRepository utenteRepository;
    private final OspiteRepository ospiteRepository;
    private final PagamentoRepository pagamentoRepository;
    private final JwtUtil jwtUtil;

    public StaffDashboardController(CameraRepository cameraRepository,
            PrenotazioneAlbergoRepository prenotazioneRepository,
            UtenteRepository utenteRepository,
            OspiteRepository ospiteRepository,
            PagamentoRepository pagamentoRepository,
            JwtUtil jwtUtil) {
        this.cameraRepository = cameraRepository;
        this.prenotazioneRepository = prenotazioneRepository;
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
                : guest != null ? guest.getEmail() : "";

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

    private String fullName(Utente user) {
        return ((user.getNome() == null ? "" : user.getNome()) + " "
                + (user.getCognome() == null ? "" : user.getCognome())).trim();
    }

    private String fullName(Ospite guest) {
        return ((guest.getNome() == null ? "" : guest.getNome()) + " "
                + (guest.getCognome() == null ? "" : guest.getCognome())).trim();
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
