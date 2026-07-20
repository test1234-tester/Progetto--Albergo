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
