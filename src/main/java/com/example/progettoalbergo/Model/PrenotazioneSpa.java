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
@Table(name = "prenotazione_spa")
public class PrenotazioneSpa {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idprenotazione_spa")
	private Long idPrenotazioneSpa;
	
	@Column(name = "fk_prenotazionespa_utente")
	private Long idUtente;
	@Column(name = "fk_prenotazionespa_pagamento")
	private Long idPagamento;
	private boolean stato;
	private LocalDate data;
	private LocalTime orario_inizio, orario_fine;

	public PrenotazioneSpa(Long idPrenotazioneSpa, Long idUtente, Long idPagamento, LocalDate data, LocalTime orario_inizio, LocalTime orario_fine) {
		setIdPrenotazioneSpa(idPrenotazioneSpa);
		setIdUtente(idUtente);
		setIdPagamento(idPagamento);
		setData(data);
		setStato(stato);
		setOrario_inizio(orario_inizio);
		setOrario_inizio(orario_fine);
	}

	public PrenotazioneSpa() {

	}

	public Long getIdPrenotazioneSpa() {
		return idPrenotazioneSpa;
	}

	public void setIdPrenotazioneSpa(Long idPrenotazioneSpa) {
		this.idPrenotazioneSpa = idPrenotazioneSpa;
	}

	public Long getIdUtente() {
		return idUtente;
	}

	public void setIdUtente(Long idUtente) {
		this.idUtente = idUtente;
	}

	public Long getIdPagamento() {
		return idPagamento;
	}

	public void setIdPagamento(Long idPagamento) {
		this.idPagamento = idPagamento;
	}

	public boolean isStato() {
		return stato;
	}

	public void setStato(boolean stato) {
		this.stato = stato;
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
