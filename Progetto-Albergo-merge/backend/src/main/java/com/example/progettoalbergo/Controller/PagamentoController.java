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

import com.example.progettoalbergo.Services.PagamentoHib;
import com.example.progettoalbergo.Model.Pagamento;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/")
public class PagamentoController {
	private PagamentoHib service;

    public PagamentoController(PagamentoHib service) {
        this.service = service;
    }

    @GetMapping("/pagamento")
    public List<Pagamento> letturatutti() {
        return service.trovaTutti();
    }
    
    @GetMapping("/pagamento/{id}")
    public Pagamento letturasingola(@PathVariable Long id) {
        return service.trovaId(id);
    }

    @PostMapping("/pagamento")
    public Pagamento aggiungi(@RequestBody Pagamento pagamento) {
        return service.salva(pagamento);
    }
    
    @RequestMapping(value="/pagamento/{id}", method=RequestMethod.PUT)
    public Pagamento aggiorna(@PathVariable Long id, @RequestBody Pagamento pagamento) {
    	pagamento.setIdPagamento(id);
        return service.salva(pagamento);
    }
    
    @RequestMapping(value="/pagamento/{id}", method=RequestMethod.DELETE)
    public void elimina(@PathVariable Long id) {
        service.elimina(id);
    }
}
