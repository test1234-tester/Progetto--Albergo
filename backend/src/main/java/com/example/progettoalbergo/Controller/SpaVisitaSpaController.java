/*
 * GUIDA DIDATTICA BACKEND
 * ---------------------------------------------------------------------------
 * CONTROLLER REST: riceve richieste HTTP, valida i dati di ingresso e coordina repository/servizi per produrre la risposta.
 * File: SpaVisitaSpaController.java
 * Segui le annotazioni Spring (@RestController, @Entity, @Service...) per capire
 * quale ruolo assume la classe nel flusso richiesta -> logica -> database.
 */
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

import com.example.progettoalbergo.Services.SpaVisitaSpaHib;
import com.example.progettoalbergo.Model.SpaVisitaSpa;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/")
public class SpaVisitaSpaController {
	private SpaVisitaSpaHib service;

    public SpaVisitaSpaController(SpaVisitaSpaHib service) {
        this.service = service;
    }

    @GetMapping("/spavisitaspa")
    public List<SpaVisitaSpa> letturatutti() {
        return service.trovaTutti();
    }
    
    @GetMapping("/spavisitaspa/{id}")
    public SpaVisitaSpa letturasingola(@PathVariable Long id) {
        return service.trovaId(id);
    }

    @PostMapping("/spavisitaspa")
    public SpaVisitaSpa aggiungi(@RequestBody SpaVisitaSpa spavisitaspa) {
        return service.salva(spavisitaspa);
    }
    
    @RequestMapping(value="/spavisitaspa/{id}", method=RequestMethod.PUT)
    public SpaVisitaSpa aggiorna(@PathVariable Long id, @RequestBody SpaVisitaSpa spavisitaspa) {
    	spavisitaspa.setIdSpaVisitaSpa(id);
        return service.salva(spavisitaspa);
    }
    
    @RequestMapping(value="/spavisitaspa/{id}", method=RequestMethod.DELETE)
    public void elimina(@PathVariable Long id) {
        service.elimina(id);
    }
}
