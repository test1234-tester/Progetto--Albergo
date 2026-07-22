/*
 * GUIDA DIDATTICA BACKEND
 * ---------------------------------------------------------------------------
 * CONTROLLER REST: riceve richieste HTTP, valida i dati di ingresso e coordina repository/servizi per produrre la risposta.
 * File: VisitaAlbergoController.java
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

import com.example.progettoalbergo.Services.VisitaAlbergoHib;
import com.example.progettoalbergo.Model.VisitaAlbergo;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/")
public class VisitaAlbergoController {
	private VisitaAlbergoHib service;

    public VisitaAlbergoController(VisitaAlbergoHib service) {
        this.service = service;
    }

    @GetMapping("/visitaalbergo")
    public List<VisitaAlbergo> letturatutti() {
        return service.trovaTutti();
    }
    
    @GetMapping("/visitaalbergo/{id}")
    public VisitaAlbergo letturasingola(@PathVariable Long id) {
        return service.trovaId(id);
    }

    @PostMapping("/visitaalbergo")
    public VisitaAlbergo aggiungi(@RequestBody VisitaAlbergo visitaalbergo) {
        return service.salva(visitaalbergo);
    }
    
    @RequestMapping(value="/visitaalbergo/{id}", method=RequestMethod.PUT)
    public VisitaAlbergo aggiorna(@PathVariable Long id, @RequestBody VisitaAlbergo visitaalbergo) {
    	visitaalbergo.setIdVisitaAlbergo(id);
        return service.salva(visitaalbergo);
    }
    
    @RequestMapping(value="/visitaalbergo/{id}", method=RequestMethod.DELETE)
    public void elimina(@PathVariable Long id) {
        service.elimina(id);
    }
}
