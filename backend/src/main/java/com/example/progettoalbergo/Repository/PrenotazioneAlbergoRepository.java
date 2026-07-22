/*
 * GUIDA DIDATTICA BACKEND
 * ---------------------------------------------------------------------------
 * REPOSITORY SPRING DATA: offre operazioni CRUD e query sul database senza scrivere manualmente il codice JDBC.
 * File: PrenotazioneAlbergoRepository.java
 * Segui le annotazioni Spring (@RestController, @Entity, @Service...) per capire
 * quale ruolo assume la classe nel flusso richiesta -> logica -> database.
 */
package com.example.progettoalbergo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.progettoalbergo.Model.PrenotazioneAlbergo;

public interface PrenotazioneAlbergoRepository extends JpaRepository<PrenotazioneAlbergo, Long> {
    List<PrenotazioneAlbergo> findByIdUtenteOrderByDataArrivoDesc(Long idUtente);
    List<PrenotazioneAlbergo> findAllByOrderByDataArrivoAsc();
    long countByIdOspite(Long idOspite);
}
