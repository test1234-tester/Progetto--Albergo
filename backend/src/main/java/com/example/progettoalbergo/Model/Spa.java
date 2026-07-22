/*
 * GUIDA DIDATTICA BACKEND
 * ---------------------------------------------------------------------------
 * ENTITY JPA: rappresenta una tabella (o relazione) del database e definisce il mapping tra Java e MySQL.
 * File: Spa.java
 * Segui le annotazioni Spring (@RestController, @Entity, @Service...) per capire
 * quale ruolo assume la classe nel flusso richiesta -> logica -> database.
 */
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

	public Spa(Long idSpa, String nome, String descrizione) {
		setIdSpa(idSpa);
		setNome(nome);
		setDescrizione(descrizione);
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
	
	
}
