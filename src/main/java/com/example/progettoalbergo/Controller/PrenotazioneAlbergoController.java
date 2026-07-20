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

import com.example.progettoalbergo.Services.PrenotazioneAlbergoHib;
import com.example.progettoalbergo.Model.PrenotazioneAlbergo;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/")
public class PrenotazioneAlbergoController {
	private PrenotazioneAlbergoHib service;

    public PrenotazioneAlbergoController(PrenotazioneAlbergoHib service) {
        this.service = service;
    }

    @GetMapping("/prenotazionealbergo")
    public List<PrenotazioneAlbergo> letturatutti() {
        return service.trovaTutti();
    }
    
    @GetMapping("/prenotazionealbergo/{id}")
    public PrenotazioneAlbergo letturasingola(@PathVariable Long id) {
        return service.trovaId(id);
    }

    @PostMapping("/prenotazionealbergo")
    public PrenotazioneAlbergo aggiungi(@RequestBody PrenotazioneAlbergo prenotazionealbergo) {
        return service.salva(prenotazionealbergo);
    }
    
    @RequestMapping(value="/prenotazionealbergo/{id}", method=RequestMethod.PUT)
    public PrenotazioneAlbergo aggiorna(@PathVariable Long id, @RequestBody PrenotazioneAlbergo prenotazionealbergo) {
    	prenotazionealbergo.setIdPrenotazioneAlbergo(id);
        return service.salva(prenotazionealbergo);
    }
    
    @RequestMapping(value="/prenotazionealbergo/{id}", method=RequestMethod.DELETE)
    public void elimina(@PathVariable Long id) {
        service.elimina(id);
    }
}
