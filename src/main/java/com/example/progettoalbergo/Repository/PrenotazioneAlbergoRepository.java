package com.example.progettoalbergo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.progettoalbergo.Model.PrenotazioneAlbergo;

public interface PrenotazioneAlbergoRepository extends JpaRepository<PrenotazioneAlbergo, Long> {
}
