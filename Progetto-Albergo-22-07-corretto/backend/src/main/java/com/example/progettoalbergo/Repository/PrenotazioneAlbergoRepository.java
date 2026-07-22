package com.example.progettoalbergo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.progettoalbergo.Model.PrenotazioneAlbergo;

public interface PrenotazioneAlbergoRepository extends JpaRepository<PrenotazioneAlbergo, Long> {
    List<PrenotazioneAlbergo> findByIdUtenteOrderByDataArrivoDesc(Long idUtente);
    List<PrenotazioneAlbergo> findAllByOrderByDataArrivoAsc();
    long countByIdOspite(Long idOspite);
}
