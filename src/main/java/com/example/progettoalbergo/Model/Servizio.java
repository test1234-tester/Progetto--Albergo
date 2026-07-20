package com.example.progettoalbergo.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "servizio")
public class Servizio {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idservizio")
	private Long idServizio;

	private String nome,descrizione;
	private Double prezzo;

	public Servizio(Long idServizio, String nome, String descrizione, Double prezzo) {
		setIdServizio(idServizio);
		setNome(nome);
		setDescrizione(descrizione);
		setPrezzo(prezzo);
	}

	public Servizio() {

	}

	public Long getIdServizio() {
		return idServizio;
	}

	public void setIdServizio(Long idServizio) {
		this.idServizio = idServizio;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public Double getPrezzo() {
		return prezzo;
	}

	public void setPrezzo(Double prezzo) {
		this.prezzo = prezzo;
	}
	
	
}
