package com.example.progettoalbergo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.progettoalbergo.Services.AdminHib;
import com.example.progettoalbergo.Model.Admin;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/")
public class AdminController {
	private AdminHib service;

    public AdminController(AdminHib service) {
        this.service = service;
    }

    @GetMapping("/admin")
    public List<Admin> letturatutti() {
        return service.trovaTutti();
    }
    
    @GetMapping("/admin/{id}")
    public Admin letturasingola(@PathVariable Long id) {
        return service.trovaId(id);
    }

    @PostMapping("/admin")
    public Admin aggiungi(@RequestBody Admin admin) {
        return service.salva(admin);
    }
    
    @RequestMapping(value="/admin/{id}", method=RequestMethod.PUT)
    public Admin aggiorna(@PathVariable Long id, @RequestBody Admin admin) {
    	admin.setIdAdmin(id);
        return service.salva(admin);
    }
    
    @RequestMapping(value="/admin/{id}", method=RequestMethod.DELETE)
    public void elimina(@PathVariable Long id) {
        service.elimina(id);
    }
}
