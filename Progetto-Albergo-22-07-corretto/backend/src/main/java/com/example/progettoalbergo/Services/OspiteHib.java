package com.example.progettoalbergo.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.progettoalbergo.Model.Ospite;
import com.example.progettoalbergo.Repository.OspiteRepository;

@Service
public class OspiteHib {

	private OspiteRepository repository;
	
	public OspiteHib(OspiteRepository repository) {
        this.repository = repository;
    }

    public List<Ospite> trovaTutti() {
        return repository.findAll();
    }

    public Ospite salva(Ospite ospite) {		
        return repository.save(ospite);
    }
    
    public Ospite trovaId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void elimina(Long id) {
        repository.deleteById(id);
    }
}
