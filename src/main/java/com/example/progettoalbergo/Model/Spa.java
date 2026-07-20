package com.example.progettoalbergo.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "spa")
public class Spa {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idspa")
	private Long idSpa;

	private String nome,descrizione;
	private Double prezzo;

	public Spa(Long idSpa, String nome, String descrizione, Double prezzo) {
		setIdSpa(idSpa);
		setNome(nome);
		setDescrizione(descrizione);
		setPrezzo(prezzo);
	}

	public Spa() {

	}

	public Long getIdSpa() {
		return idSpa;
	}

	public void setIdSpa(Long idSpa) {
		this.idSpa = idSpa;
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
