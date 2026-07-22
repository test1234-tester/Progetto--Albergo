package com.example.progettoalbergo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.progettoalbergo.Services.CameraComfortHib;
import com.example.progettoalbergo.Model.CameraComfort;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/")
public class CameraComfortController {
	private CameraComfortHib service;

    public CameraComfortController(CameraComfortHib service) {
        this.service = service;
    }

    @GetMapping("/cameracomfort")
    public List<CameraComfort> letturatutti() {
        return service.trovaTutti();
    }
    
    @GetMapping("/cameracomfort/{id}")
    public CameraComfort letturasingola(@PathVariable Long id) {
        return service.trovaId(id);
    }

    @PostMapping("/cameracomfort")
    public CameraComfort aggiungi(@RequestBody CameraComfort cameracomfort) {
        return service.salva(cameracomfort);
    }
    
    @RequestMapping(value="/cameracomfort/{id}", method=RequestMethod.PUT)
    public CameraComfort aggiorna(@PathVariable Long id, @RequestBody CameraComfort cameracomfort) {
    	cameracomfort.setIdCameraComfort(id);
        return service.salva(cameracomfort);
    }
    
    @RequestMapping(value="/cameracomfort/{id}", method=RequestMethod.DELETE)
    public void elimina(@PathVariable Long id) {
        service.elimina(id);
    }
}
