/*
 * GUIDA DIDATTICA BACKEND
 * ---------------------------------------------------------------------------
 * ENTITY JPA: rappresenta una tabella (o relazione) del database e definisce il mapping tra Java e MySQL.
 * File: Camera.java
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
@Table(name = "camera")
public class Camera {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idcamera")
	private Long idCamera;

	private String nome,descrizione,immagine;
	private boolean stato;
	private int occupanti;
	private Double prezzoPerNotte;

	public Camera(Long idCamera, String nome, String descrizione, String immagine, boolean stato,int occupanti, Double prezzoPerNotte) {
		setIdCamera(idCamera);
		setNome(nome);
		setDescrizione(descrizione);
		setImmagine(immagine);
		setStato(stato);
		setOccupanti(occupanti);
		setPrezzoPerNotte(prezzoPerNotte);
	}

	public Camera() {

	}

	public Long getIdCamera() {
		return idCamera;
	}

	public void setIdCamera(Long idCamera) {
		this.idCamera = idCamera;
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

	public String getImmagine() {
		return immagine;
	}

	public void setImmagine(String immagine) {
		this.immagine = immagine;
	}

	public boolean isStato() {
		return stato;
	}

	public void setStato(boolean stato) {
		this.stato = stato;
	}

	public int getOccupanti() {
		return occupanti;
	}

	public void setOccupanti(int occupanti) {
		this.occupanti = occupanti;
	}

	public Double getPrezzoPerNotte() {
		return prezzoPerNotte;
	}

	public void setPrezzoPerNotte(Double prezzoPerNotte) {
		this.prezzoPerNotte = prezzoPerNotte;
	}

	

}
