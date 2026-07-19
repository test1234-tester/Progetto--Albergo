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

	private String nome;
	private String descrizione;
	private String immagine;
	private Double prezzoPerNotte;

	public Camera(Long idCamera, String nome, String descrizione, String immagine, Double prezzoPerNotte) {
		setIdCamera(idCamera);
		setNome(nome);
		setDescrizione(descrizione);
		setImmagine(immagine);
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

	public Double getPrezzoPerNotte() {
		return prezzoPerNotte;
	}

	public void setPrezzoPerNotte(Double prezzoPerNotte) {
		this.prezzoPerNotte = prezzoPerNotte;
	}

}
