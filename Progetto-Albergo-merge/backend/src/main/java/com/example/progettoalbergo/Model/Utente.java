package com.example.progettoalbergo.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name = "utente")
public class Utente {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idutente")
    private Long idUtente;

    private String nome,cognome,username,cellulare,email;

    // WRITE_ONLY: leggibile dal JSON in ingresso (register/login), ma non finisce MAI
    // in una risposta JSON (es. GET /utente non deve mai restituire l'hash)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    
    public Utente(Long idUtente, String nome, String cognome, String username, String cellulare, String email, String password) {
    	setIdUtente(idUtente);
		setNome(nome);
		setCognome(cognome);
		setUsername(username);
		setCellulare(cellulare);
		setEmail(email);
		setPassword(password);
	}
	
	public Utente() {
		
	}

	public Long getIdUtente() {
		return idUtente;
	}

	public void setIdUtente(Long idUtente) {
		this.idUtente = idUtente;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
    
}
