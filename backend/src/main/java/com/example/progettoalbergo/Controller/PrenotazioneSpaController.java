/*
 * GUIDA DIDATTICA BACKEND
 * ---------------------------------------------------------------------------
 * CONTROLLER REST: riceve richieste HTTP, valida i dati di ingresso e coordina repository/servizi per produrre la risposta.
 * File: PrenotazioneSpaController.java
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

import com.example.progettoalbergo.Services.PrenotazioneSpaHib;
import com.example.progettoalbergo.Model.PrenotazioneSpa;

import com.example.progettoalbergo.Services.PagamentoHib;
import com.example.progettoalbergo.Model.Pagamento;

import com.example.progettoalbergo.Security.JwtUtil;
import org.springframework.web.bind.annotation.RequestHeader;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/")
public class PrenotazioneSpaController {
	private PrenotazioneSpaHib prenotazioneService;
	private PagamentoHib pagamentoService;
	private final JwtUtil jwtUtil;

    public PrenotazioneSpaController(PrenotazioneSpaHib prenotazioneService, PagamentoHib pagamentoService,JwtUtil jwtUtil) {
    	this.prenotazioneService = prenotazioneService;
        this.pagamentoService = pagamentoService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/prenotazionespa")
    public List<PrenotazioneSpa> letturatutti() {
        return prenotazioneService.trovaTutti();
    }
    
    @GetMapping("/prenotazionespa/{id}")
    public PrenotazioneSpa letturasingola(@PathVariable Long id) {
        return prenotazioneService.trovaId(id);
    }

    /*@PostMapping("/prenotazionespa")
    public PrenotazioneSpa aggiungi(@RequestBody PrenotazioneSpa prenotazionespa) {
        return prenotazioneService.salva(prenotazionespa);
    }*/
    
    @RequestMapping(value="/prenotazionespa/{id}", method=RequestMethod.PUT)
    public PrenotazioneSpa aggiorna(@PathVariable Long id, @RequestBody PrenotazioneSpa prenotazionespa) {
    	prenotazionespa.setIdPrenotazioneSpa(id);
        return prenotazioneService.salva(prenotazionespa);
    }
    
    @RequestMapping(value="/prenotazionespa/{id}", method=RequestMethod.DELETE)
    public void elimina(@PathVariable Long id) {
    	prenotazioneService.elimina(id);
    }
    
    @PostMapping("/prenotazionespa")
    public PrenotazioneSpa aggiunginuovo(
            @RequestBody PrenotazioneSpa prenotazione,
            @RequestHeader("Authorization") String header) {


        if(header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("Token non valido");
        }

        String token = header.substring(7);

        Long idUtente = jwtUtil.extractUserId(token);

        // CREAZIONE PAGAMENTO

        Pagamento pagamento = new Pagamento();

        pagamento.setTotale(200.0);
        String tipo = prenotazione.getTipoPagamento();

        if(tipo == null) {
            throw new RuntimeException("Tipo pagamento mancante");
        }

        pagamento.setTipo(tipo);
        // carta = pagato
        // bonifico = non pagato
        if("card".equals(prenotazione.getTipoPagamento())) {
            pagamento.setStato(true);
        } else {
            pagamento.setStato(false);
        }
        
        Pagamento nuovoPagamento = pagamentoService.salva(pagamento);

        // COLLEGAMENTO PRENOTAZIONE SPA

        prenotazione.setIdUtente(idUtente);

        prenotazione.setIdPagamento(nuovoPagamento.getIdPagamento());

        return prenotazioneService.salva(prenotazione);
    }

}
