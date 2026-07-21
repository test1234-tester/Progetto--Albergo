package com.example.progettoalbergo.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name = "ospite")
public class Ospite {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idospite")
    private Long idOspite;

    private String nome,cognome,cellulare,email;
	
	public Ospite(Long idOspite, String nome, String cognome, String cellulare, String email) {
    	setIdOspite(idOspite);
		setNome(nome);
		setCognome(cognome);
		setCellulare(cellulare);
		setEmail(email);
	}
	
	public Ospite() {
		
	}

	public Long getIdOspite() {
		return idOspite;
	}

	public void setIdOspite(Long idOspite) {
		this.idOspite = idOspite;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getCellulare() {
		return cellulare;
	}

	public void setCellulare(String cellulare) {
		this.cellulare = cellulare;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
