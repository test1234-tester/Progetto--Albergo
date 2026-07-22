/*
 * GUIDA DIDATTICA BACKEND
 * ---------------------------------------------------------------------------
 * SERVICE/HIB: raccoglie operazioni di accesso ai dati e logica riutilizzabile tra controller.
 * File: ComfortHib.java
 * Segui le annotazioni Spring (@RestController, @Entity, @Service...) per capire
 * quale ruolo assume la classe nel flusso richiesta -> logica -> database.
 */
package com.example.progettoalbergo.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.progettoalbergo.Model.Comfort;
import com.example.progettoalbergo.Repository.ComfortRepository;

@Service
public class ComfortHib {
private ComfortRepository repository;
	
	public ComfortHib(ComfortRepository repository) {
        this.repository = repository;
    }

    public List<Comfort> trovaTutti() {
        return repository.findAll();
    }

    public Comfort salva(Comfort comfort) {
        return repository.save(comfort);
    }
    
    public Comfort trovaId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void elimina(Long id) {
        repository.deleteById(id);
    }
}
