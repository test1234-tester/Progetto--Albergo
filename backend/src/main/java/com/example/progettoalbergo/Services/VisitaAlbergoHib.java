/*
 * GUIDA DIDATTICA BACKEND
 * ---------------------------------------------------------------------------
 * SERVICE/HIB: raccoglie operazioni di accesso ai dati e logica riutilizzabile tra controller.
 * File: VisitaAlbergoHib.java
 * Segui le annotazioni Spring (@RestController, @Entity, @Service...) per capire
 * quale ruolo assume la classe nel flusso richiesta -> logica -> database.
 */
package com.example.progettoalbergo.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.progettoalbergo.Model.VisitaAlbergo;
import com.example.progettoalbergo.Repository.VisitaAlbergoRepository;

@Service
public class VisitaAlbergoHib {
	private VisitaAlbergoRepository repository;

	public VisitaAlbergoHib(VisitaAlbergoRepository repository) {
        this.repository = repository;
    }

    public List<VisitaAlbergo> trovaTutti() {
        return repository.findAll();
    }

    public VisitaAlbergo salva(VisitaAlbergo visitaalbergo) {
        return repository.save(visitaalbergo);
    }

    public VisitaAlbergo trovaId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void elimina(Long id) {
        repository.deleteById(id);
    }
}
