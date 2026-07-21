package com.example.progettoalbergo.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.progettoalbergo.Model.PrenotazioneServizio;
import com.example.progettoalbergo.Repository.PrenotazioneServizioRepository;

@Service
public class PrenotazioneServizioHib {
	private PrenotazioneServizioRepository repository;

	public PrenotazioneServizioHib(PrenotazioneServizioRepository repository) {
        this.repository = repository;
    }

    public List<PrenotazioneServizio> trovaTutti() {
        return repository.findAll();
    }

    public PrenotazioneServizio salva(PrenotazioneServizio prenotazioneservizio) {
        return repository.save(prenotazioneservizio);
    }

    public PrenotazioneServizio trovaId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void elimina(Long id) {
        repository.deleteById(id);
    }
}
