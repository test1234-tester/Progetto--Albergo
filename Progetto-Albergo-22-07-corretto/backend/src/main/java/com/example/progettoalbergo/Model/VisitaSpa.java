package com.example.progettoalbergo.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "visita_spa")
public class VisitaSpa {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idvisita_spa")
	private Long idVisitaSpa;

	@Column(name = "fk_visitaspa_ospite")
	private Long idOspite;
	@Column(name = "fk_visitaspa_pagamento")
	private Long idPagamento;
	private LocalDate data;
	private LocalTime orario_inizio, orario_fine;

	public VisitaSpa(Long idVisitaSpa, Long idOspite, Long idPagamento, LocalDate data, LocalTime orario_inizio, LocalTime orario_fine) {
		setIdVisitaSpa(idVisitaSpa);
		setIdOspite(idOspite);
		setIdPagamento(idPagamento);
		setData(data);
		setOrario_inizio(orario_inizio);
		setOrario_inizio(orario_fine);
	}

	public VisitaSpa() {

	}

	public Long getIdVisitaSpa() {
		return idVisitaSpa;
	}

	public void setIdVisitaSpa(Long idVisitaSpa) {
		this.idVisitaSpa = idVisitaSpa;
	}

	public Long getIdOspite() {
		return idOspite;
	}

	public void setIdOspite(Long idOspite) {
		this.idOspite = idOspite;
	}

	public Long getIdPagamento() {
		return idPagamento;
	}

	public void setIdPagamento(Long idPagamento) {
		this.idPagamento = idPagamento;
	}

	public LocalDate getData() {
		return data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	public LocalTime getOrario_inizio() {
		return orario_inizio;
	}

	public void setOrario_inizio(LocalTime orario_inizio) {
		this.orario_inizio = orario_inizio;
	}

	public LocalTime getOrario_fine() {
		return orario_fine;
	}

	public void setOrario_fine(LocalTime orario_fine) {
		this.orario_fine = orario_fine;
	}
	
	
}
