package com.example.progettoalbergo.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.progettoalbergo.Model.SpaVisitaSpa;
import com.example.progettoalbergo.Repository.SpaVisitaSpaRepository;

@Service
public class SpaVisitaSpaHib {
	private SpaVisitaSpaRepository repository;

	public SpaVisitaSpaHib(SpaVisitaSpaRepository repository) {
        this.repository = repository;
    }

    public List<SpaVisitaSpa> trovaTutti() {
        return repository.findAll();
    }

    public SpaVisitaSpa salva(SpaVisitaSpa spavisitaspa) {
        return repository.save(spavisitaspa);
    }

    public SpaVisitaSpa trovaId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void elimina(Long id) {
        repository.deleteById(id);
    }
}
