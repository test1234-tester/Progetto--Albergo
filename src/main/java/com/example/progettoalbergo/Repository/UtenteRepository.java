package com.example.progettoalbergo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.progettoalbergo.Model.Utente;

public interface UtenteRepository extends JpaRepository<Utente, Long> {
	Optional<Utente> findByEmail(String email);
	boolean existsByEmail(String email);
}
