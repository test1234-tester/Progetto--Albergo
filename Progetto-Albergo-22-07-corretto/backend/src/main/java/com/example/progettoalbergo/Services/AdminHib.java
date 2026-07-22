package com.example.progettoalbergo.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.progettoalbergo.Model.Admin;
import com.example.progettoalbergo.Repository.AdminRepository;

@Service
public class AdminHib {

	private AdminRepository repository;
	
	public AdminHib(AdminRepository repository) {
        this.repository = repository;
    }

    public List<Admin> trovaTutti() {
        return repository.findAll();
    }

    public Admin salva(Admin admin) {
        return repository.save(admin);
    }
    
    public Admin trovaId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void elimina(Long id) {
        repository.deleteById(id);
    }
}
