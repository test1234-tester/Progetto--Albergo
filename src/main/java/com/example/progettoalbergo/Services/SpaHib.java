package com.example.progettoalbergo.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.progettoalbergo.Model.Spa;
import com.example.progettoalbergo.Repository.SpaRepository;

@Service
public class SpaHib {
	private SpaRepository repository;

	public SpaHib(SpaRepository repository) {
        this.repository = repository;
    }

    public List<Spa> trovaTutti() {
        return repository.findAll();
    }

    public Spa salva(Spa spa) {
        return repository.save(spa);
    }

    public Spa trovaId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void elimina(Long id) {
        repository.deleteById(id);
    }
}
