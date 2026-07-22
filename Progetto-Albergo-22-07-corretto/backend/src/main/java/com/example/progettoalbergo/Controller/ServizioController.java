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

import com.example.progettoalbergo.Services.ServizioHib;
import com.example.progettoalbergo.Model.Servizio;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/")
public class ServizioController {
	private ServizioHib service;

    public ServizioController(ServizioHib service) {
        this.service = service;
    }

    @GetMapping("/servizio")
    public List<Servizio> letturatutti() {
        return service.trovaTutti();
    }
    
    @GetMapping("/servizio/{id}")
    public Servizio letturasingola(@PathVariable Long id) {
        return service.trovaId(id);
    }

    @PostMapping("/servizio")
    public Servizio aggiungi(@RequestBody Servizio servizio) {
        return service.salva(servizio);
    }
    
    @RequestMapping(value="/servizio/{id}", method=RequestMethod.PUT)
    public Servizio aggiorna(@PathVariable Long id, @RequestBody Servizio servizio) {
    	servizio.setIdServizio(id);
        return service.salva(servizio);
    }
    
    @RequestMapping(value="/servizio/{id}", method=RequestMethod.DELETE)
    public void elimina(@PathVariable Long id) {
        service.elimina(id);
    }
}
