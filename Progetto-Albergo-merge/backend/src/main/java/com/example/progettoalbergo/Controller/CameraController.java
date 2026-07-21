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

import com.example.progettoalbergo.Services.CameraHib;
import com.example.progettoalbergo.Model.Camera;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/")
public class CameraController {
	private CameraHib service;

    public CameraController(CameraHib service) {
        this.service = service;
    }

    @GetMapping("/camera")
    public List<Camera> letturatutti() {
        return service.trovaTutti();
    }
    
    @GetMapping("/camera/{id}")
    public Camera letturasingola(@PathVariable Long id) {
        return service.trovaId(id);
    }

    @PostMapping("/camera")
    public Camera aggiungi(@RequestBody Camera camera) {
        return service.salva(camera);
    }
    
    @RequestMapping(value="/camera/{id}", method=RequestMethod.PUT)
    public Camera aggiorna(@PathVariable Long id, @RequestBody Camera camera) {
    	camera.setIdCamera(id);
        return service.salva(camera);
    }
    
    @RequestMapping(value="/camera/{id}", method=RequestMethod.DELETE)
    public void elimina(@PathVariable Long id) {
        service.elimina(id);
    }
}
