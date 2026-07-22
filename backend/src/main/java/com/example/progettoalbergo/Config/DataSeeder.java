/*
 * GUIDA DIDATTICA BACKEND
 * ---------------------------------------------------------------------------
 * CONFIGURAZIONE: inizializza dati o allinea lo schema DB quando l’applicazione parte.
 * File: DataSeeder.java
 * Segui le annotazioni Spring (@RestController, @Entity, @Service...) per capire
 * quale ruolo assume la classe nel flusso richiesta -> logica -> database.
 */
package com.example.progettoalbergo.Config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.progettoalbergo.Model.Admin;
import com.example.progettoalbergo.Model.Camera;
import com.example.progettoalbergo.Repository.AdminRepository;
import com.example.progettoalbergo.Repository.CameraRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CameraRepository cameraRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(CameraRepository cameraRepository,
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder) {
        this.cameraRepository = cameraRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedRooms();
        seedStaffAccount();
    }

    /*
     * BLOCCO DIDATTICO — Inizializzazione e rinomina camere.
     *
     * Se il DB è vuoto vengono create 50 camere. Se invece esistono già,
     * aggiorniamo SOLO i vecchi nomi generici del tipo "Camera 1", "Camera 2"...
     * In questo modo un eventuale nome personalizzato inserito dallo staff non viene
     * sovrascritto a ogni avvio dell'applicazione.
     */
    private void seedRooms() {
        if (cameraRepository.count() == 0) {
            for (int i = 1; i <= 50; i++) {
                Camera camera = new Camera();
                camera.setNome(buildRoomName(i));
                camera.setDescrizione(buildRoomDescription(i));
                camera.setImmagine("camere/camera-" + i + ".jpg");
                camera.setPrezzoPerNotte(60.0 + (i % 10) * 15);
                camera.setStato(false);
                camera.setOccupanti(0);
                cameraRepository.save(camera);
            }
            return;
        }

        // Migrazione leggera dei record già presenti nel database.
        cameraRepository.findAll().forEach(camera -> {
            if (camera.getIdCamera() == null) {
                return;
            }

            String currentName = camera.getNome();

            boolean hasGenericName =
                    currentName == null
                    || currentName.isBlank()
                    || currentName.matches("(?i)^Camera\\s+\\d+$")
                    || currentName.matches(".*\\s\\d{3}$");

            if (hasGenericName) {
                int id = camera.getIdCamera().intValue();
                camera.setNome(buildRoomName(id));

                String description = camera.getDescrizione();
                if (description == null
                        || description.isBlank()
                        || description.startsWith("Camera confortevole al piano")) {
                    camera.setDescrizione(buildRoomDescription(id));
                }

                cameraRepository.save(camera);
            }
        });
    }

    /*
     * BLOCCO DIDATTICO — Trasforma l'id tecnico (1..50) in un numero camera
     * leggibile da hotel: 101..110, 201..210, ... 501..510.
     */
    private int displayRoomNumber(int id) {
        int floor = ((id - 1) / 10) + 1;
        int position = ((id - 1) % 10) + 1;
        return floor * 100 + position;
    }

    /*
     * BLOCCO DIDATTICO — Assegna una tipologia coerente alla posizione della
     * camera sul piano. Le tipologie si ripetono sui cinque piani, mentre il
     * numero rende ogni camera inequivocabile.
     */
    private String roomType(int id) {
        int position = ((id - 1) % 10) + 1;
        return switch (position) {
            case 1 -> "Camera Comfort";
            case 2 -> "Camera Armonia";
            case 3 -> "Camera Superior";
            case 4 -> "Camera Deluxe";
            case 5 -> "Camera Wellness";
            case 6 -> "Junior Suite";
            case 7 -> "Camera Panorama";
            case 8 -> "Suite Minerva";
            case 9 -> "Executive Spa Suite";
            default -> "Camera Classic";
        };
    }

    private String buildRoomName(int id) {
        return roomType(id);
    }

    private String buildRoomDescription(int id) {
        int floor = ((id - 1) / 10) + 1;
        return roomType(id) + " al piano " + floor
                + ", pensata per un soggiorno confortevole e rilassante.";
    }

    private void seedStaffAccount() {
        String email = "reception@albergo.it";
        if (adminRepository.existsByEmailIgnoreCase(email)) {
            return;
        }

        Admin admin = new Admin();
        admin.setUsername("Reception");
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode("Staff123!"));
        adminRepository.save(admin);
    }
}
