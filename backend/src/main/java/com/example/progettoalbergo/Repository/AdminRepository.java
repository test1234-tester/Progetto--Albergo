/*
 * GUIDA DIDATTICA BACKEND
 * ---------------------------------------------------------------------------
 * REPOSITORY SPRING DATA: offre operazioni CRUD e query sul database senza scrivere manualmente il codice JDBC.
 * File: AdminRepository.java
 * Segui le annotazioni Spring (@RestController, @Entity, @Service...) per capire
 * quale ruolo assume la classe nel flusso richiesta -> logica -> database.
 */
package com.example.progettoalbergo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.progettoalbergo.Model.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}
