package com.example.progettoalbergo.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "visita_albergo")
public class VisitaAlbergo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idvisita_albergo")
	private Long idVisitaAlbergo;

	@Column(name = "fk_visitaalbergo_ospite")
	private Long idOspite;
	@Column(name = "fk_visitaalbergo_camera")
	private Long idCamera;
	@Column(name = "fk_visitaalbergo_pagamento")
	private Long idPagamento;

	public VisitaAlbergo(Long idVisitaAlbergo, Long idOspite, Long idCamera, Long idPagamento) {
		setIdVisitaAlbergo(idVisitaAlbergo);
		setIdOspite(idOspite);
		setIdCamera(idCamera);
		setIdPagamento(idPagamento);
	}

	public VisitaAlbergo() {

	}

	public Long getIdVisitaAlbergo() {
		return idVisitaAlbergo;
	}

	public void setIdVisitaAlbergo(Long idVisitaAlbergo) {
		this.idVisitaAlbergo = idVisitaAlbergo;
	}

	public Long getIdOspite() {
		return idOspite;
	}

	public void setIdOspite(Long idOspite) {
		this.idOspite = idOspite;
	}

	public Long getIdCamera() {
		return idCamera;
	}

	public void setIdCamera(Long idCamera) {
		this.idCamera = idCamera;
	}

	public Long getIdPagamento() {
		return idPagamento;
	}

	public void setIdPagamento(Long idPagamento) {
		this.idPagamento = idPagamento;
	}
	
	
}
