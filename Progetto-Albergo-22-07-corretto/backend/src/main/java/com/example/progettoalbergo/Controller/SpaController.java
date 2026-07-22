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

import com.example.progettoalbergo.Services.SpaHib;
import com.example.progettoalbergo.Model.Spa;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/")
public class SpaController {
	private SpaHib service;

    public SpaController(SpaHib service) {
        this.service = service;
    }

    @GetMapping("/spa")
    public List<Spa> letturatutti() {
        return service.trovaTutti();
    }
    
    @GetMapping("/spa/{id}")
    public Spa letturasingola(@PathVariable Long id) {
        return service.trovaId(id);
    }

    @PostMapping("/spa")
    public Spa aggiungi(@RequestBody Spa spa) {
        return service.salva(spa);
    }
    
    @RequestMapping(value="/spa/{id}", method=RequestMethod.PUT)
    public Spa aggiorna(@PathVariable Long id, @RequestBody Spa spa) {
    	spa.setIdSpa(id);
        return service.salva(spa);
    }
    
    @RequestMapping(value="/spa/{id}", method=RequestMethod.DELETE)
    public void elimina(@PathVariable Long id) {
        service.elimina(id);
    }
}
