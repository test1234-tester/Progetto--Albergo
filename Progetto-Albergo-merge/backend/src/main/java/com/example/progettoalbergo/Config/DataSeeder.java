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

    private void seedRooms() {
        if (cameraRepository.count() > 0) {
            return;
        }

        for (int i = 1; i <= 50; i++) {
            Camera camera = new Camera();
            camera.setNome("Camera " + i);
            camera.setDescrizione("Camera confortevole al piano " + ((i - 1) / 10 + 1)
                    + ", ideale per il tuo soggiorno.");
            camera.setImmagine("camere/camera-" + i + ".jpg");
            camera.setPrezzoPerNotte(60.0 + (i % 10) * 15);
            camera.setStato(false);
            camera.setOccupanti(0);
            cameraRepository.save(camera);
        }
    }

    private void seedStaffAccount() {
        String email = "reception@albergo.it";
        if (adminRepository.existsByEmail(email)) {
            return;
        }

        Admin admin = new Admin();
        admin.setUsername("Reception");
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode("Staff123!"));
        adminRepository.save(admin);
    }
}
