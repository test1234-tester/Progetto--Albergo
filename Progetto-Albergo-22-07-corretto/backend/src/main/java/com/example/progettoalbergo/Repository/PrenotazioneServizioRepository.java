package com.example.progettoalbergo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.progettoalbergo.Model.PrenotazioneServizio;

public interface PrenotazioneServizioRepository extends JpaRepository<PrenotazioneServizio, Long> {
    void deleteByIdPrenotazioneAlbergo(Long idPrenotazioneAlbergo);
}
