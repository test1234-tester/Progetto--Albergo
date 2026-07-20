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

import com.example.progettoalbergo.Services.PrenotazioneSpaHib;
import com.example.progettoalbergo.Model.PrenotazioneSpa;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/")
public class PrenotazioneSpaController {
	private PrenotazioneSpaHib service;

    public PrenotazioneSpaController(PrenotazioneSpaHib service) {
        this.service = service;
    }

    @GetMapping("/prenotazionespa")
    public List<PrenotazioneSpa> letturatutti() {
        return service.trovaTutti();
    }
    
    @GetMapping("/prenotazionespa/{id}")
    public PrenotazioneSpa letturasingola(@PathVariable Long id) {
        return service.trovaId(id);
    }

    @PostMapping("/prenotazionespa")
    public PrenotazioneSpa aggiungi(@RequestBody PrenotazioneSpa prenotazionespa) {
        return service.salva(prenotazionespa);
    }
    
    @RequestMapping(value="/prenotazionespa/{id}", method=RequestMethod.PUT)
    public PrenotazioneSpa aggiorna(@PathVariable Long id, @RequestBody PrenotazioneSpa prenotazionespa) {
    	prenotazionespa.setIdPrenotazioneSpa(id);
        return service.salva(prenotazionespa);
    }
    
    @RequestMapping(value="/prenotazionespa/{id}", method=RequestMethod.DELETE)
    public void elimina(@PathVariable Long id) {
        service.elimina(id);
    }
}
