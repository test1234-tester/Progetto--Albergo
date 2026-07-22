/*
 * GUIDA DIDATTICA BACKEND
 * ---------------------------------------------------------------------------
 * CONTROLLER REST: riceve richieste HTTP, valida i dati di ingresso e coordina repository/servizi per produrre la risposta.
 * File: AuthController.java
 * Segui le annotazioni Spring (@RestController, @Entity, @Service...) per capire
 * quale ruolo assume la classe nel flusso richiesta -> logica -> database.
 */
package com.example.progettoalbergo.Controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.progettoalbergo.Model.Admin;
import com.example.progettoalbergo.Model.Utente;
import com.example.progettoalbergo.Repository.AdminRepository;
import com.example.progettoalbergo.Repository.UtenteRepository;
import com.example.progettoalbergo.Security.JwtUtil;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final String ROLE_CLIENTE = "CLIENTE";
    private static final String ROLE_STAFF = "STAFF";

    private final UtenteRepository utenteRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UtenteRepository utenteRepository,
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil) {
        this.utenteRepository = utenteRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Utente utente) {
        if (utente == null || blank(utente.getEmail()) || blank(utente.getPassword())
                || utente.getPassword().length() < 6) {
            return ResponseEntity.badRequest().body("Email e password valida sono obbligatorie");
        }

        String email = normalizeEmail(utente.getEmail());
        if (utenteRepository.existsByEmailIgnoreCase(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email gia' registrata");
        }

        utente.setEmail(email);
        utente.setPassword(passwordEncoder.encode(utente.getPassword()));
        Utente salvato = utenteRepository.save(utente);

        String token = jwtUtil.generateToken(salvato.getIdUtente(), salvato.getEmail(), ROLE_CLIENTE);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authResponse(token, publicUser(salvato, ROLE_CLIENTE)));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenziali) {
        String emailInput = value(credenziali, "email");
        String passwordInput = value(credenziali, "password");
        if (blank(emailInput) || blank(passwordInput)) {
            return unauthorized();
        }

        String email = normalizeEmail(emailInput);
        Utente utente = utenteRepository.findByEmailIgnoreCase(email).orElse(null);
        if (utente == null || !customerPasswordMatchesAndMigrate(utente, passwordInput)) {
            return unauthorized();
        }

        String token = jwtUtil.generateToken(utente.getIdUtente(), utente.getEmail(), ROLE_CLIENTE);
        return ResponseEntity.ok(authResponse(token, publicUser(utente, ROLE_CLIENTE)));
    }

    @PostMapping("/staff/login")
    public ResponseEntity<?> staffLogin(@RequestBody Map<String, String> credenziali) {
        String emailInput = value(credenziali, "email");
        String passwordInput = value(credenziali, "password");
        if (blank(emailInput) || blank(passwordInput)) {
            return unauthorized();
        }

        String email = normalizeEmail(emailInput);
        Admin admin = adminRepository.findByEmailIgnoreCase(email).orElse(null);
        if (admin == null || !passwordMatchesAndMigrate(admin, passwordInput)) {
            return unauthorized();
        }

        String token = jwtUtil.generateToken(admin.getIdAdmin(), admin.getEmail(), ROLE_STAFF);
        Map<String, Object> staff = new LinkedHashMap<>();
        staff.put("idUtente", admin.getIdAdmin());
        staff.put("nome", admin.getUsername());
        staff.put("cognome", "");
        staff.put("username", admin.getUsername());
        staff.put("cellulare", "");
        staff.put("email", admin.getEmail());
        staff.put("role", ROLE_STAFF);
        return ResponseEntity.ok(authResponse(token, staff));
    }

    private boolean customerPasswordMatchesAndMigrate(Utente utente, String rawPassword) {
        String stored = utente.getPassword();
        if (stored == null) {
            return false;
        }
        if (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$")) {
            return passwordEncoder.matches(rawPassword, stored);
        }
        // Compatibilita' con eventuali vecchie password demo salvate in chiaro.
        if (stored.equals(rawPassword)) {
            utente.setPassword(passwordEncoder.encode(rawPassword));
            utenteRepository.save(utente);
            return true;
        }
        return false;
    }

    private boolean passwordMatchesAndMigrate(Admin admin, String rawPassword) {
        String stored = admin.getPassword();
        if (stored == null) {
            return false;
        }
        if (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$")) {
            return passwordEncoder.matches(rawPassword, stored);
        }
        if (stored.equals(rawPassword)) {
            admin.setPassword(passwordEncoder.encode(rawPassword));
            adminRepository.save(admin);
            return true;
        }
        return false;
    }

    private Map<String, Object> publicUser(Utente utente, String role) {
        Map<String, Object> user = new LinkedHashMap<>();
        user.put("idUtente", utente.getIdUtente());
        user.put("nome", utente.getNome());
        user.put("cognome", utente.getCognome());
        user.put("username", utente.getUsername());
        user.put("cellulare", utente.getCellulare());
        user.put("email", utente.getEmail());
        user.put("role", role);
        return user;
    }

    private Map<String, Object> authResponse(String token, Map<String, Object> user) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("token", token);
        response.put("user", user);
        return response;
    }

    private String value(Map<String, String> body, String key) {
        return body == null ? null : body.get(key);
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private ResponseEntity<String> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email o password non corretti");
    }
}
