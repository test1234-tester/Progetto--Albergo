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

import com.example.progettoalbergo.Services.SpaPrenotazioneSpaHib;
import com.example.progettoalbergo.Model.SpaPrenotazioneSpa;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/")
public class SpaPrenotazioneSpaController {
	private SpaPrenotazioneSpaHib service;

    public SpaPrenotazioneSpaController(SpaPrenotazioneSpaHib service) {
        this.service = service;
    }

    @GetMapping("/spaprenotazionespa")
    public List<SpaPrenotazioneSpa> letturatutti() {
        return service.trovaTutti();
    }
    
    @GetMapping("/spaprenotazionespa/{id}")
    public SpaPrenotazioneSpa letturasingola(@PathVariable Long id) {
        return service.trovaId(id);
    }

    @PostMapping("/spaprenotazionespa")
    public SpaPrenotazioneSpa aggiungi(@RequestBody SpaPrenotazioneSpa spaprenotazionespa) {
        return service.salva(spaprenotazionespa);
    }
    
    @RequestMapping(value="/spaprenotazionespa/{id}", method=RequestMethod.PUT)
    public SpaPrenotazioneSpa aggiorna(@PathVariable Long id, @RequestBody SpaPrenotazioneSpa spaprenotazionespa) {
    	spaprenotazionespa.setIdSpaPrenotazioneSpa(id);
        return service.salva(spaprenotazionespa);
    }
    
    @RequestMapping(value="/spaprenotazionespa/{id}", method=RequestMethod.DELETE)
    public void elimina(@PathVariable Long id) {
        service.elimina(id);
    }
}
