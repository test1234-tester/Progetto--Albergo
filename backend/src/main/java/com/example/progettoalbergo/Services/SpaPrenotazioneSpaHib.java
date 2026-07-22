/*
 * GUIDA DIDATTICA BACKEND
 * ---------------------------------------------------------------------------
 * SERVICE/HIB: raccoglie operazioni di accesso ai dati e logica riutilizzabile tra controller.
 * File: SpaPrenotazioneSpaHib.java
 * Segui le annotazioni Spring (@RestController, @Entity, @Service...) per capire
 * quale ruolo assume la classe nel flusso richiesta -> logica -> database.
 */
package com.example.progettoalbergo.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.progettoalbergo.Model.SpaPrenotazioneSpa;
import com.example.progettoalbergo.Repository.SpaPrenotazioneSpaRepository;

@Service
public class SpaPrenotazioneSpaHib {
	private SpaPrenotazioneSpaRepository repository;

	public SpaPrenotazioneSpaHib(SpaPrenotazioneSpaRepository repository) {
        this.repository = repository;
    }

    public List<SpaPrenotazioneSpa> trovaTutti() {
        return repository.findAll();
    }

    public SpaPrenotazioneSpa salva(SpaPrenotazioneSpa spaPrenotazionespa) {
        return repository.save(spaPrenotazionespa);
    }

    public SpaPrenotazioneSpa trovaId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void elimina(Long id) {
        repository.deleteById(id);
    }
}
