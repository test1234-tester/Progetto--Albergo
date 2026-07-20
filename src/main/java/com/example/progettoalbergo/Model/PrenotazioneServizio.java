package com.example.progettoalbergo.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "prenotazione_servizio")
public class PrenotazioneServizio {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idprenotazione_servizio")
	private Long idPrenotazioneServizio;

	@Column(name = "fk_prenotazione_servizio_prenotazione")
	private Long idPrenotazioneAlbergo;
	@Column(name = "fk_prenotazione_servizio_servizio")
	private Long idServizio;

	public PrenotazioneServizio(Long idPrenotazioneServizio, Long idPrenotazioneAlbergo, Long idServizio) {
		setIdPrenotazioneServizio(idPrenotazioneServizio);
		setIdPrenotazioneAlbergo(idPrenotazioneAlbergo);
		setIdServizio(idServizio);
	}

	public PrenotazioneServizio() {

	}

	public Long getIdPrenotazioneServizio() {
		return idPrenotazioneServizio;
	}

	public void setIdPrenotazioneServizio(Long idPrenotazioneServizio) {
		this.idPrenotazioneServizio = idPrenotazioneServizio;
	}

	public Long getIdPrenotazioneAlbergo() {
		return idPrenotazioneAlbergo;
	}

	public void setIdPrenotazioneAlbergo(Long idPrenotazioneAlbergo) {
		this.idPrenotazioneAlbergo = idPrenotazioneAlbergo;
	}

	public Long getIdServizio() {
		return idServizio;
	}

	public void setIdServizio(Long idServizio) {
		this.idServizio = idServizio;
	}
	
	
}
