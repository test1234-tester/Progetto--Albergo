package com.example.progettoalbergo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.progettoalbergo.Model.VisitaAlbergo;

public interface VisitaAlbergoRepository extends JpaRepository<VisitaAlbergo, Long> {
}
