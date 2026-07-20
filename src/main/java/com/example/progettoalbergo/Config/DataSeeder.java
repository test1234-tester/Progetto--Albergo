//Riempie il DB all'avvio se vuoto

package com.example.progettoalbergo.Config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.progettoalbergo.Model.Camera;
import com.example.progettoalbergo.Repository.CameraRepository;

// Popola 50 camere all'avvio, solo se il DB e' vuoto (evita duplicati ad ogni riavvio)
@Component
public class DataSeeder implements CommandLineRunner {

	private final CameraRepository repository;

	public DataSeeder(CameraRepository repository) {
		this.repository = repository;
	}

	@Override
	public void run(String... args) {
		if (repository.count() > 0) {
			return;
		}

		for (int i = 1; i <= 50; i++) {
			Camera camera = new Camera();
			camera.setNome("Camera " + i);
			camera.setDescrizione("Camera confortevole al piano " + ((i - 1) / 10 + 1) + ", ideale per il tuo soggiorno.");
			camera.setImmagine("assets/rooms/camera-" + ((i % 5) + 1) + ".jpg");
			camera.setPrezzoPerNotte(60.0 + (i % 10) * 15);
			repository.save(camera);
		}
	}
}
