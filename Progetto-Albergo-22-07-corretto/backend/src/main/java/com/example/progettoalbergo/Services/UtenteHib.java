package com.example.progettoalbergo.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.progettoalbergo.Model.Utente;
import com.example.progettoalbergo.Repository.UtenteRepository;

@Service
public class UtenteHib {
	
	private UtenteRepository repository;
	
	public UtenteHib(UtenteRepository repository) {
        this.repository = repository;
    }

    public List<Utente> trovaTutti() {
        return repository.findAll();
    }

    public Utente salva(Utente utente) {
        return repository.save(utente);
    }
    
    public Utente trovaId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void elimina(Long id) {
        repository.deleteById(id);
    }
}
