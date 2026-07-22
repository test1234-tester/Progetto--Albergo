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

import com.example.progettoalbergo.Services.VisitaSpaHib;
import com.example.progettoalbergo.Model.VisitaSpa;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/")
public class VisitaSpaController {
	private VisitaSpaHib service;

    public VisitaSpaController(VisitaSpaHib service) {
        this.service = service;
    }

    @GetMapping("/visitaspa")
    public List<VisitaSpa> letturatutti() {
        return service.trovaTutti();
    }
    
    @GetMapping("/visitaspa/{id}")
    public VisitaSpa letturasingola(@PathVariable Long id) {
        return service.trovaId(id);
    }

    @PostMapping("/visitaspa")
    public VisitaSpa aggiungi(@RequestBody VisitaSpa visitaspa) {
        return service.salva(visitaspa);
    }
    
    @RequestMapping(value="/visitaspa/{id}", method=RequestMethod.PUT)
    public VisitaSpa aggiorna(@PathVariable Long id, @RequestBody VisitaSpa visitaspa) {
    	visitaspa.setIdVisitaSpa(id);
        return service.salva(visitaspa);
    }
    
    @RequestMapping(value="/visitaspa/{id}", method=RequestMethod.DELETE)
    public void elimina(@PathVariable Long id) {
        service.elimina(id);
    }
}
