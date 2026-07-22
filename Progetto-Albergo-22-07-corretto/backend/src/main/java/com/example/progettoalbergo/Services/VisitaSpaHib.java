package com.example.progettoalbergo.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.progettoalbergo.Model.VisitaSpa;
import com.example.progettoalbergo.Repository.VisitaSpaRepository;

@Service
public class VisitaSpaHib {
	
	private VisitaSpaRepository repository;

	public VisitaSpaHib(VisitaSpaRepository repository) {
        this.repository = repository;
    }

    public List<VisitaSpa> trovaTutti() {
        return repository.findAll();
    }

    public VisitaSpa salva(VisitaSpa visitaspa) {
        return repository.save(visitaspa);
    }

    public VisitaSpa trovaId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void elimina(Long id) {
        repository.deleteById(id);
    }
}
