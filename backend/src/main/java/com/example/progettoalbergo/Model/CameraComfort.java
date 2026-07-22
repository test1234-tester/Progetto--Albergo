/*
 * GUIDA DIDATTICA BACKEND
 * ---------------------------------------------------------------------------
 * ENTITY JPA: rappresenta una tabella (o relazione) del database e definisce il mapping tra Java e MySQL.
 * File: CameraComfort.java
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
@Table(name = "camera_comfort")
public class CameraComfort {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idcamera_comfort")
    private Long idCameraComfort;
	@Column(name = "fk_camera_comfort_camera")
    private Long idCamera;
	@Column(name = "fk_camera_comfort_comfort")
	private Long idComfort;
    
    public CameraComfort(Long idCameraComfort, Long idCamera, Long idComfort) {
    	setIdCameraComfort(idCameraComfort);
    	setIdCamera(idCamera);
    	setIdComfort(idComfort);
	}
	
	public CameraComfort() {
		
	}

	public Long getIdCameraComfort() {
		return idCameraComfort;
	}

	public void setIdCameraComfort(Long idCameraComfort) {
		this.idCameraComfort = idCameraComfort;
	}

	public Long getIdCamera() {
		return idCamera;
	}

	public void setIdCamera(Long idcamera) {
		this.idCamera = idcamera;
	}

	public Long getIdComfort() {
		return idComfort;
	}

	public void setIdComfort(Long idComfort) {
		this.idComfort = idComfort;
	}
	
	
}
