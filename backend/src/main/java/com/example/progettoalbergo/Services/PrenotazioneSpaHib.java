/*
 * GUIDA DIDATTICA BACKEND
 * ---------------------------------------------------------------------------
 * SERVICE/HIB: raccoglie operazioni di accesso ai dati e logica riutilizzabile tra controller.
 * File: PrenotazioneSpaHib.java
 * Segui le annotazioni Spring (@RestController, @Entity, @Service...) per capire
 * quale ruolo assume la classe nel flusso richiesta -> logica -> database.
 */
package com.example.progettoalbergo.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.progettoalbergo.Model.PrenotazioneSpa;
import com.example.progettoalbergo.Repository.PrenotazioneSpaRepository;

@Service
public class PrenotazioneSpaHib {
	private PrenotazioneSpaRepository repository;

	public PrenotazioneSpaHib(PrenotazioneSpaRepository repository) {
        this.repository = repository;
    }

    public List<PrenotazioneSpa> trovaTutti() {
        return repository.findAll();
    }

    public PrenotazioneSpa salva(PrenotazioneSpa prenotazionespa) {
        return repository.save(prenotazionespa);
    }

    public PrenotazioneSpa trovaId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void elimina(Long id) {
        repository.deleteById(id);
    }
}
