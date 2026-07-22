/*
 * GUIDA DIDATTICA BACKEND
 * ---------------------------------------------------------------------------
 * CONTROLLER REST: riceve richieste HTTP, valida i dati di ingresso e coordina repository/servizi per produrre la risposta.
 * File: OspiteController.java
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

import com.example.progettoalbergo.Services.OspiteHib;
import com.example.progettoalbergo.Model.Ospite;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/")
public class OspiteController {
	private OspiteHib service;

    public OspiteController(OspiteHib service) {
        this.service = service;
    }

    @GetMapping("/ospite")
    public List<Ospite> letturatutti() {
        return service.trovaTutti();
    }
    
    @GetMapping("/ospite/{id}")
    public Ospite letturasingola(@PathVariable Long id) {
        return service.trovaId(id);
    }

    @PostMapping("/ospite")
    public Ospite aggiungi(@RequestBody Ospite ospite) {
        return service.salva(ospite);
    }
    
    @RequestMapping(value="/ospite/{id}", method=RequestMethod.PUT)
    public Ospite aggiorna(@PathVariable Long id, @RequestBody Ospite ospite) {
    	ospite.setIdOspite(id);
        return service.salva(ospite);
    }
    
    @RequestMapping(value="/ospite/{id}", method=RequestMethod.DELETE)
    public void elimina(@PathVariable Long id) {
        service.elimina(id);
    }
}
