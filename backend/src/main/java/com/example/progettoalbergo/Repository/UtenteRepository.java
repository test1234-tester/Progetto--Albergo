/*
 * GUIDA DIDATTICA BACKEND
 * ---------------------------------------------------------------------------
 * REPOSITORY SPRING DATA: offre operazioni CRUD e query sul database senza scrivere manualmente il codice JDBC.
 * File: UtenteRepository.java
 * Segui le annotazioni Spring (@RestController, @Entity, @Service...) per capire
 * quale ruolo assume la classe nel flusso richiesta -> logica -> database.
 */
package com.example.progettoalbergo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.progettoalbergo.Model.Utente;

public interface UtenteRepository extends JpaRepository<Utente, Long> {
    Optional<Utente> findByEmail(String email);
    Optional<Utente> findByEmailIgnoreCase(String email);
    boolean existsByEmail(String email);
    boolean existsByEmailIgnoreCase(String email);
}
