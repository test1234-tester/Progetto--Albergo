package com.example.progettoalbergo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.progettoalbergo.Model.Pagamento;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    long countByStato(boolean stato);
}
