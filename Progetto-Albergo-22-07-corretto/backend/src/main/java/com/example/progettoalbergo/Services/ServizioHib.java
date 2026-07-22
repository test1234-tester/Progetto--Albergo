package com.example.progettoalbergo.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.progettoalbergo.Model.Servizio;
import com.example.progettoalbergo.Repository.ServizioRepository;

@Service
public class ServizioHib {
	private ServizioRepository repository;

	public ServizioHib(ServizioRepository repository) {
        this.repository = repository;
    }

    public List<Servizio> trovaTutti() {
        return repository.findAll();
    }

    public Servizio salva(Servizio servizio) {
        return repository.save(servizio);
    }

    public Servizio trovaId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void elimina(Long id) {
        repository.deleteById(id);
    }
}
