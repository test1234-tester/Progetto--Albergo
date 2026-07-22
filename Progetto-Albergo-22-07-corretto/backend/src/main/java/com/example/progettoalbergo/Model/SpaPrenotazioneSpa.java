package com.example.progettoalbergo.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "spa_prenotaziones_spa")
public class SpaPrenotazioneSpa {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idspa_prenotaziones_spa")
	private Long idSpaPrenotazioneSpa;

	@Column(name = "fk_spa_prenotazione_prenotazionespa")
	private Long idPrenotazioneSpa;
	@Column(name = "fk_spa_prenotazione_spa")
	private Long idSpa;

	public SpaPrenotazioneSpa(Long idSpaPrenotazioneSpa, Long idPrenotazioneSpa, Long idSpa) {
		setIdSpaPrenotazioneSpa(idSpaPrenotazioneSpa);
		setIdPrenotazioneSpa(idPrenotazioneSpa);
		setIdSpa(idSpa);
	}

	public SpaPrenotazioneSpa() {

	}

	public Long getIdSpaPrenotazioneSpa() {
		return idSpaPrenotazioneSpa;
	}

	public void setIdSpaPrenotazioneSpa(Long idSpaPrenotazioneSpa) {
		this.idSpaPrenotazioneSpa = idSpaPrenotazioneSpa;
	}

	public Long getIdPrenotazioneSpa() {
		return idPrenotazioneSpa;
	}

	public void setIdPrenotazioneSpa(Long idPrenotazioneSpa) {
		this.idPrenotazioneSpa = idPrenotazioneSpa;
	}

	public Long getIdSpa() {
		return idSpa;
	}

	public void setIdSpa(Long idSpa) {
		this.idSpa = idSpa;
	}
	
	
}
