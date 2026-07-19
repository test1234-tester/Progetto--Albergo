package com.example.progettoalbergo.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.progettoalbergo.Model.Utente;
import com.example.progettoalbergo.Repository.UtenteRepository;
import com.example.progettoalbergo.Security.JwtUtil;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/auth")
public class AuthController {

	private final UtenteRepository utenteRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	public AuthController(UtenteRepository utenteRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
		this.utenteRepository = utenteRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody Utente utente) {
		if (utenteRepository.existsByEmail(utente.getEmail())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Email gia' registrata");
		}

		utente.setPassword(passwordEncoder.encode(utente.getPassword()));
		Utente salvato = utenteRepository.save(utente);

		String token = jwtUtil.generateToken(salvato.getIdUtente(), salvato.getEmail());
		return ResponseEntity.ok(new AuthResponse(token, salvato));
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Utente credenziali) {
		Utente utente = utenteRepository.findByEmail(credenziali.getEmail()).orElse(null);

		if (utente == null || !passwordEncoder.matches(credenziali.getPassword(), utente.getPassword())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email o password non corretti");
		}

		String token = jwtUtil.generateToken(utente.getIdUtente(), utente.getEmail());
		return ResponseEntity.ok(new AuthResponse(token, utente));
	}
}
