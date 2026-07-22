/*
 * GUIDA DIDATTICA BACKEND
 * ---------------------------------------------------------------------------
 * CONTROLLER REST: riceve richieste HTTP, valida i dati di ingresso e coordina repository/servizi per produrre la risposta.
 * File: PrenotazioneServizioController.java
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

import com.example.progettoalbergo.Services.PrenotazioneServizioHib;
import com.example.progettoalbergo.Model.PrenotazioneServizio;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/")
public class PrenotazioneServizioController {
	private PrenotazioneServizioHib service;

    public PrenotazioneServizioController(PrenotazioneServizioHib service) {
        this.service = service;
    }

    @GetMapping("/prenotazioneservizio")
    public List<PrenotazioneServizio> letturatutti() {
        return service.trovaTutti();
    }
    
    @GetMapping("/prenotazioneservizio/{id}")
    public PrenotazioneServizio letturasingola(@PathVariable Long id) {
        return service.trovaId(id);
    }

    @PostMapping("/prenotazioneservizio")
    public PrenotazioneServizio aggiungi(@RequestBody PrenotazioneServizio prenotazioneservizio) {
        return service.salva(prenotazioneservizio);
    }
    
    @RequestMapping(value="/prenotazioneservizio/{id}", method=RequestMethod.PUT)
    public PrenotazioneServizio aggiorna(@PathVariable Long id, @RequestBody PrenotazioneServizio prenotazioneservizio) {
    	prenotazioneservizio.setIdPrenotazioneServizio(id);
        return service.salva(prenotazioneservizio);
    }
    
    @RequestMapping(value="/prenotazioneservizio/{id}", method=RequestMethod.DELETE)
    public void elimina(@PathVariable Long id) {
        service.elimina(id);
    }
}
