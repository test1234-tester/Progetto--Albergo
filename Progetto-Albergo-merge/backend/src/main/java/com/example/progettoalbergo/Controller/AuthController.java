package com.example.progettoalbergo.Controller;

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
                .body(new AuthResponse(token, toAuthenticatedUser(salvato)));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest credenziali) {
        if (!validCredentials(credenziali)) {
            return unauthorized();
        }

        String email = normalizeEmail(credenziali.email());
        Utente utente = utenteRepository.findByEmailIgnoreCase(email).orElse(null);
        if (utente == null || !customerPasswordMatchesAndMigrate(utente, credenziali.password())) {
            return unauthorized();
        }

        String token = jwtUtil.generateToken(utente.getIdUtente(), utente.getEmail(), ROLE_CLIENTE);
        return ResponseEntity.ok(new AuthResponse(token, toAuthenticatedUser(utente)));
    }

    @PostMapping("/staff/login")
    public ResponseEntity<?> staffLogin(@RequestBody LoginRequest credenziali) {
        if (!validCredentials(credenziali)) {
            return unauthorized();
        }

        String email = normalizeEmail(credenziali.email());
        Admin admin = adminRepository.findByEmail(email).orElse(null);
        if (admin == null || !passwordMatchesAndMigrate(admin, credenziali.password())) {
            return unauthorized();
        }

        String token = jwtUtil.generateToken(admin.getIdAdmin(), admin.getEmail(), ROLE_STAFF);
        AuthenticatedUser staff = new AuthenticatedUser(
                admin.getIdAdmin(),
                admin.getUsername(),
                "",
                admin.getUsername(),
                "",
                admin.getEmail(),
                ROLE_STAFF);
        return ResponseEntity.ok(new AuthResponse(token, staff));
    }

    private boolean customerPasswordMatchesAndMigrate(Utente utente, String rawPassword) {
        String stored = utente.getPassword();
        if (stored == null) {
            return false;
        }
        if (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$")) {
            return passwordEncoder.matches(rawPassword, stored);
        }
        // Compatibilita' con i vecchi record demo eventualmente salvati in chiaro.
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

    private AuthenticatedUser toAuthenticatedUser(Utente utente) {
        return new AuthenticatedUser(
                utente.getIdUtente(),
                utente.getNome(),
                utente.getCognome(),
                utente.getUsername(),
                utente.getCellulare(),
                utente.getEmail(),
                ROLE_CLIENTE);
    }

    private boolean validCredentials(LoginRequest credentials) {
        return credentials != null && !blank(credentials.email()) && !blank(credentials.password());
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
