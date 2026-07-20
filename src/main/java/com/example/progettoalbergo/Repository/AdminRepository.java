package com.example.progettoalbergo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.progettoalbergo.Model.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}
