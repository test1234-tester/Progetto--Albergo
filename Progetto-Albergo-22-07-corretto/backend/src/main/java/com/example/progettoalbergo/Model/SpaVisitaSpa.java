package com.example.progettoalbergo.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "spa_visita_spa")
public class SpaVisitaSpa {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idspa_visita_spa")
	private Long idSpaVisitaSpa;

	@Column(name = "fk_spavisitaspa_visitaspa")
	private Long idSpaVisita;
	@Column(name = "fk_spavisitaspa_spa")
	private Long idSpa;

	public SpaVisitaSpa(Long idSpaVisitaSpa,Long idSpaVisita,Long idSpa) {
		setIdSpaVisitaSpa(idSpaVisitaSpa);
		setIdSpaVisita(idSpaVisita);
		setIdSpa(idSpa);
	}

	public SpaVisitaSpa() {

	}

	public Long getIdSpaVisitaSpa() {
		return idSpaVisitaSpa;
	}

	public void setIdSpaVisitaSpa(Long idSpaVisitaSpa) {
		this.idSpaVisitaSpa = idSpaVisitaSpa;
	}

	public Long getIdSpaVisita() {
		return idSpaVisita;
	}

	public void setIdSpaVisita(Long idSpaVisita) {
		this.idSpaVisita = idSpaVisita;
	}

	public Long getIdSpa() {
		return idSpa;
	}

	public void setIdSpa(Long idSpa) {
		this.idSpa = idSpa;
	}
	
}
