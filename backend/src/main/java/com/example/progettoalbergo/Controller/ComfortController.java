/*
 * GUIDA DIDATTICA BACKEND
 * ---------------------------------------------------------------------------
 * CONTROLLER REST: riceve richieste HTTP, valida i dati di ingresso e coordina repository/servizi per produrre la risposta.
 * File: ComfortController.java
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

import com.example.progettoalbergo.Services.ComfortHib;
import com.example.progettoalbergo.Model.Comfort;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/")
public class ComfortController {
	private ComfortHib service;

    public ComfortController(ComfortHib service) {
        this.service = service;
    }

    @GetMapping("/comfort")
    public List<Comfort> letturatutti() {
        return service.trovaTutti();
    }
    
    @GetMapping("/comfort/{id}")
    public Comfort letturasingola(@PathVariable Long id) {
        return service.trovaId(id);
    }

    @PostMapping("/comfort")
    public Comfort aggiungi(@RequestBody Comfort comfort) {
        return service.salva(comfort);
    }
    
    @RequestMapping(value="/comfort/{id}", method=RequestMethod.PUT)
    public Comfort aggiorna(@PathVariable Long id, @RequestBody Comfort comfort) {
    	comfort.setIdComfort(id);
        return service.salva(comfort);
    }
    
    @RequestMapping(value="/comfort/{id}", method=RequestMethod.DELETE)
    public void elimina(@PathVariable Long id) {
        service.elimina(id);
    }
}
