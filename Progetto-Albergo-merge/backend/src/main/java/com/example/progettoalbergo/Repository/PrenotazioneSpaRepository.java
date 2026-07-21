package com.example.progettoalbergo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.progettoalbergo.Model.PrenotazioneSpa;

public interface PrenotazioneSpaRepository extends JpaRepository<PrenotazioneSpa, Long> {
}
