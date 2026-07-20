package com.example.progettoalbergo.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.progettoalbergo.Model.Pagamento;
import com.example.progettoalbergo.Repository.PagamentoRepository;

@Service
public class PagamentoHib {
	private PagamentoRepository repository;

	public PagamentoHib(PagamentoRepository repository) {
        this.repository = repository;
    }

    public List<Pagamento> trovaTutti() {
        return repository.findAll();
    }

    public Pagamento salva(Pagamento pagamento) {
        return repository.save(pagamento);
    }

    public Pagamento trovaId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void elimina(Long id) {
        repository.deleteById(id);
    }
}
