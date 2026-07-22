/*
 * GUIDA DIDATTICA BACKEND
 * ---------------------------------------------------------------------------
 * REPOSITORY SPRING DATA: offre operazioni CRUD e query sul database senza scrivere manualmente il codice JDBC.
 * File: CameraRepository.java
 * Segui le annotazioni Spring (@RestController, @Entity, @Service...) per capire
 * quale ruolo assume la classe nel flusso richiesta -> logica -> database.
 */
package com.example.progettoalbergo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.progettoalbergo.Model.Camera;

public interface CameraRepository extends JpaRepository<Camera, Long> {
}
