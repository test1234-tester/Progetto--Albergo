/*
 * GUIDA DIDATTICA BACKEND
 * ---------------------------------------------------------------------------
 * CONTROLLER REST: riceve richieste HTTP, valida i dati di ingresso e coordina repository/servizi per produrre la risposta.
 * File: UtenteController.java
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

import com.example.progettoalbergo.Services.UtenteHib;
import com.example.progettoalbergo.Model.Utente;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/")
public class UtenteController {
	private UtenteHib service;

    public UtenteController(UtenteHib service) {
        this.service = service;
    }

    @GetMapping("/utente")
    public List<Utente> letturatutti() {
        return service.trovaTutti();
    }
    
    @GetMapping("/utente/{id}")
    public Utente letturasingola(@PathVariable Long id) {
        return service.trovaId(id);
    }

    @PostMapping("/utente")
    public Utente aggiungi(@RequestBody Utente utente) {
        return service.salva(utente);
    }
    
    @RequestMapping(value="/utente/{id}", method=RequestMethod.PUT)
    public Utente aggiorna(@PathVariable Long id, @RequestBody Utente utente) {
    	utente.setIdUtente(id);
        return service.salva(utente);
    }
    
    @RequestMapping(value="/utente/{id}", method=RequestMethod.DELETE)
    public void elimina(@PathVariable Long id) {
        service.elimina(id);
    }
}
