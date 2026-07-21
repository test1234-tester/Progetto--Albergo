package com.example.progettoalbergo.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.progettoalbergo.Model.PrenotazioneAlbergo;
import com.example.progettoalbergo.Repository.PrenotazioneAlbergoRepository;

@Service
public class PrenotazioneAlbergoHib {
private PrenotazioneAlbergoRepository repository;
	
	public PrenotazioneAlbergoHib(PrenotazioneAlbergoRepository repository) {
        this.repository = repository;
    }

    public List<PrenotazioneAlbergo> trovaTutti() {
        return repository.findAll();
    }

    public PrenotazioneAlbergo salva(PrenotazioneAlbergo prenotazionealbergo) {
        return repository.save(prenotazionealbergo);
    }
    
    public PrenotazioneAlbergo trovaId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void elimina(Long id) {
        repository.deleteById(id);
    }
}
