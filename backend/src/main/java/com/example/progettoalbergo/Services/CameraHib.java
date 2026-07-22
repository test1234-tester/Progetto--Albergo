/*
 * GUIDA DIDATTICA BACKEND
 * ---------------------------------------------------------------------------
 * SERVICE/HIB: raccoglie operazioni di accesso ai dati e logica riutilizzabile tra controller.
 * File: CameraHib.java
 * Segui le annotazioni Spring (@RestController, @Entity, @Service...) per capire
 * quale ruolo assume la classe nel flusso richiesta -> logica -> database.
 */
package com.example.progettoalbergo.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.progettoalbergo.Model.Camera;
import com.example.progettoalbergo.Repository.CameraRepository;

@Service
public class CameraHib {

	private CameraRepository repository;

	public CameraHib(CameraRepository repository) {
        this.repository = repository;
    }

    public List<Camera> trovaTutti() {
        return repository.findAll();
    }

    public Camera salva(Camera camera) {
        return repository.save(camera);
    }

    public Camera trovaId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void elimina(Long id) {
        repository.deleteById(id);
    }
}
