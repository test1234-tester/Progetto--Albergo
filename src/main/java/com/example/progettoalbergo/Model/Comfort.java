package com.example.progettoalbergo.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name = "comfort")
public class Comfort {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idcomfort")
    private Long idComfort;

    private String nome,descrizione;
    
    public Comfort(Long idComfort, String nome, String descrizione) {
    	setIdComfort(idComfort);
		setNome(nome);
		setDescrizione(descrizione);
	}
	
	public Comfort() {
		
	}

	public Long getIdComfort() {
		return idComfort;
	}

	public void setIdComfort(Long idComfort) {
		this.idComfort = idComfort;
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
