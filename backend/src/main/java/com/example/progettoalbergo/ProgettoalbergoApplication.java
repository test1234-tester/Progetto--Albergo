/*
 * GUIDA DIDATTICA BACKEND
 * ---------------------------------------------------------------------------
 * BOOTSTRAP SPRING: punto di ingresso/configurazione dell’applicazione backend.
 * File: ProgettoalbergoApplication.java
 * Segui le annotazioni Spring (@RestController, @Entity, @Service...) per capire
 * quale ruolo assume la classe nel flusso richiesta -> logica -> database.
 */
package com.example.progettoalbergo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProgettoalbergoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProgettoalbergoApplication.class, args);
	}

}
