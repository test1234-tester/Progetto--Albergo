package com.example.progettoalbergo.Controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.progettoalbergo.Repository.CameraRepository;
import com.example.progettoalbergo.Repository.UtenteRepository;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class HealthController {

    private final JdbcTemplate jdbcTemplate;
    private final UtenteRepository utenteRepository;
    private final CameraRepository cameraRepository;

    public HealthController(JdbcTemplate jdbcTemplate,
            UtenteRepository utenteRepository,
            CameraRepository cameraRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.utenteRepository = utenteRepository;
        this.cameraRepository = cameraRepository;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", "UP");
        result.put("database", jdbcTemplate.queryForObject("SELECT DATABASE()", String.class));
        result.put("utenti", utenteRepository.count());
        result.put("camere", cameraRepository.count());
        result.put("dashboardUtente", "/dashboard/user");
        return result;
    }
}
