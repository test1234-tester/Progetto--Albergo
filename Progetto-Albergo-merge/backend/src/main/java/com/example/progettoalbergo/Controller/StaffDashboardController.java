package com.example.progettoalbergo.Controller;

import java.time.LocalDate;
import java.util.Comparator;
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
    private final PagamentoRepository pagamentoRepository;
    private final JwtUtil jwtUtil;

    public StaffDashboardController(CameraRepository cameraRepository,
            PrenotazioneAlbergoRepository prenotazioneRepository,
            UtenteRepository utenteRepository,
            PagamentoRepository pagamentoRepository,
            JwtUtil jwtUtil) {
        this.cameraRepository = cameraRepository;
        this.prenotazioneRepository = prenotazioneRepository;
        this.utenteRepository = utenteRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/dashboard")
    public StaffDashboardResponse dashboard(HttpServletRequest request) {
        requireStaff(request);

        List<StaffBookingResponse> bookings = prenotazioneRepository
                .findAllByOrderByDataArrivoAsc()
                .stream()
                .map(this::toBooking)
                .toList();
        List<StaffRoomResponse> rooms = cameraRepository.findAll().stream()
                .sorted(Comparator.comparing(Camera::getIdCamera))
                .map(this::toRoom)
                .toList();
        List<StaffUserResponse> users = utenteRepository.findAll().stream()
                .sorted(Comparator.comparing(Utente::getCognome,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .map(this::toUser)
                .toList();

        StaffStatsResponse stats = new StaffStatsResponse(
                cameraRepository.count(),
                rooms.stream().filter(StaffRoomResponse::occupata).count(),
                prenotazioneRepository.count(),
                utenteRepository.count(),
                pagamentoRepository.countByStato(false));

        return new StaffDashboardResponse(stats, bookings, rooms, users);
    }

    @PatchMapping("/bookings/{bookingId}/status")
    public StaffBookingResponse updateBookingStatus(@PathVariable Long bookingId,
            @RequestBody BookingStatusUpdateRequest payload,
            HttpServletRequest request) {
        requireStaff(request);
        PrenotazioneAlbergo booking = prenotazioneRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Prenotazione non trovata"));
        booking.setStato(payload.confermata());
        return toBooking(prenotazioneRepository.save(booking));
    }

    @PatchMapping("/rooms/{roomId}/status")
    public StaffRoomResponse updateRoomStatus(@PathVariable Long roomId,
            @RequestBody RoomStatusUpdateRequest payload,
            HttpServletRequest request) {
        requireStaff(request);
        Camera camera = cameraRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Camera non trovata"));
        camera.setStato(payload.occupata());
        camera.setOccupanti(payload.occupata() ? Math.max(1, payload.occupanti()) : 0);
        return toRoom(cameraRepository.save(camera));
    }

    private StaffBookingResponse toBooking(PrenotazioneAlbergo booking) {
        Camera camera = booking.getIdCamera() == null ? null
                : cameraRepository.findById(booking.getIdCamera()).orElse(null);
        Utente user = booking.getidUtente() == null ? null
                : utenteRepository.findById(booking.getidUtente()).orElse(null);

        return new StaffBookingResponse(
                booking.getIdPrenotazioneAlbergo(),
                booking.getidUtente(),
                user == null ? "Utente non disponibile" : fullName(user),
                user == null ? "" : user.getEmail(),
                camera == null ? "Camera non disponibile" : camera.getNome(),
                booking.getIdCamera(),
                booking.getDataArrivo(),
                booking.getDataPartenza(),
                booking.getNominativo(),
                booking.isStato());
    }

    private StaffRoomResponse toRoom(Camera room) {
        return new StaffRoomResponse(
                room.getIdCamera(),
                room.getIdCamera(),
                room.getNome(),
                room.getPrezzoPerNotte(),
                room.isStato(),
                room.getOccupanti());
    }

    private StaffUserResponse toUser(Utente user) {
        return new StaffUserResponse(
                user.getIdUtente(),
                fullName(user),
                user.getUsername(),
                user.getEmail(),
                user.getCellulare());
    }

    private String fullName(Utente user) {
        return ((user.getNome() == null ? "" : user.getNome()) + " "
                + (user.getCognome() == null ? "" : user.getCognome())).trim();
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

    public record StaffDashboardResponse(
            StaffStatsResponse stats,
            List<StaffBookingResponse> bookings,
            List<StaffRoomResponse> rooms,
            List<StaffUserResponse> users) {
    }

    public record StaffStatsResponse(
            long camereTotali,
            long camereOccupate,
            long prenotazioniTotali,
            long clientiRegistrati,
            long pagamentiInPendenza) {
    }

    public record StaffBookingResponse(
            Long id,
            Long idUtente,
            String cliente,
            String email,
            String camera,
            Long numeroCamera,
            LocalDate dataArrivo,
            LocalDate dataPartenza,
            String nominativo,
            boolean confermata) {
    }

    public record StaffRoomResponse(
            Long id,
            Long numero,
            String nome,
            Double prezzoPerNotte,
            boolean occupata,
            int occupanti) {
    }

    public record StaffUserResponse(
            Long id,
            String nominativo,
            String username,
            String email,
            String cellulare) {
    }

    public record BookingStatusUpdateRequest(boolean confermata) {
    }

    public record RoomStatusUpdateRequest(boolean occupata, int occupanti) {
    }
}
