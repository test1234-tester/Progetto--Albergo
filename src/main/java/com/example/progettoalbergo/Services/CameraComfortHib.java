package com.example.progettoalbergo.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.progettoalbergo.Model.CameraComfort;
import com.example.progettoalbergo.Repository.CameraComfortRepository;

@Service
public class CameraComfortHib {
private CameraComfortRepository repository;
	
	public CameraComfortHib(CameraComfortRepository repository) {
        this.repository = repository;
    }

    public List<CameraComfort> trovaTutti() {
        return repository.findAll();
    }

    public CameraComfort salva(CameraComfort cameracomfort) {
        return repository.save(cameracomfort);
    }
    
    public CameraComfort trovaId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void elimina(Long id) {
        repository.deleteById(id);
    }
}
